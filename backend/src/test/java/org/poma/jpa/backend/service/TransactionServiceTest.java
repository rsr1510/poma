package org.poma.jpa.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.poma.jpa.backend.dto.SellRequest;
import org.poma.jpa.backend.dto.TransactionRequest;
import org.poma.jpa.backend.dto.TransactionSummaryDto;
import org.poma.jpa.backend.entity.*;
import org.poma.jpa.backend.repo.AssetRepo;
import org.poma.jpa.backend.repo.HoldingsRepo;
import org.poma.jpa.backend.repo.TransactionRepo;
import org.poma.jpa.backend.repo.UserRepo;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private AssetRepo assetRepo;

    @Mock
    private HoldingsRepo holdingsRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Assets testAsset;
    private Holdings testHolding;
    private TransactionRequest buyRequest;
    private SellRequest sellRequest;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User");
        ReflectionTestUtils.setField(testUser, "id", 1L);

        testAsset = new Assets();
        ReflectionTestUtils.setField(testAsset, "id", 1L);
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");

        testHolding = new Holdings();
        ReflectionTestUtils.setField(testHolding, "id", 1L);
        testHolding.setPortfolioOwner(testUser);
        testHolding.setAsset(testAsset);
        testHolding.setQuantity(new BigDecimal("100"));
        testHolding.setAvgBuyPrice(new BigDecimal("150.00"));
        testHolding.setCurrentPrice(new BigDecimal("160.00"));

        buyRequest = new TransactionRequest();
        buyRequest.setSymbol("AAPL");
        buyRequest.setQuantity(new BigDecimal("50"));
        buyRequest.setPricePerUnit(new BigDecimal("155.00"));
        buyRequest.setPlatform("Robinhood");
        buyRequest.setFees(new BigDecimal("5.00"));
        buyRequest.setTotalCost(new BigDecimal("7755.00"));

        sellRequest = new SellRequest();
        sellRequest.setSymbol("AAPL");
        sellRequest.setQuantity(new BigDecimal("30"));
    }

    @Test
    @DisplayName("Should successfully buy asset when asset exists and user exists")
    void testBuySuccess() {
        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(holdingsRepo.findByAsset(testAsset)).thenReturn(Optional.empty());
        when(transactionRepo.save(any(Transactions.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(holdingsRepo.save(any(Holdings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transactions result = transactionService.buy(buyRequest);

        assertNotNull(result);
        assertEquals(testAsset, result.getAsset());
        assertEquals(TransactionType.BUY, result.getTransactionType());
        assertEquals("Robinhood", result.getPlatform());
        assertEquals(new BigDecimal("50"), result.getQuantity());
        assertEquals(new BigDecimal("155.00"), result.getPricePerUnit());
        assertEquals(new BigDecimal("5.00"), result.getFees());
        assertEquals(new BigDecimal("7755.00"), result.getTotalCost());

        verify(assetRepo, times(1)).findBySymbol("AAPL");
        verify(userRepo, times(1)).findById(1L);
        verify(holdingsRepo, times(1)).findByAsset(testAsset);
        verify(transactionRepo, times(1)).save(any(Transactions.class));
        verify(holdingsRepo, times(1)).save(any(Holdings.class));
    }

    @Test
    @DisplayName("Should update existing holding when buying existing asset")
    void testBuyUpdatesExistingHolding() {
        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(holdingsRepo.findByAsset(testAsset)).thenReturn(Optional.of(testHolding));
        when(transactionRepo.save(any(Transactions.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(holdingsRepo.save(any(Holdings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transactions result = transactionService.buy(buyRequest);

        assertNotNull(result);
        verify(holdingsRepo, times(1)).save(testHolding);
        
        BigDecimal expectedNewQuantity = new BigDecimal("150");
        BigDecimal expectedNewAvgPrice = new BigDecimal("151.67");
        assertEquals(expectedNewQuantity, testHolding.getQuantity());
        assertEquals(expectedNewAvgPrice.setScale(2, RoundingMode.HALF_UP), testHolding.getAvgBuyPrice());
    }

    @Test
    @DisplayName("Should throw RuntimeException when asset not found during buy")
    void testBuyAssetNotFound() {
        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> transactionService.buy(buyRequest)
        );

        assertEquals("Asset not found", exception.getMessage());
        verify(assetRepo, times(1)).findBySymbol("AAPL");
        verify(userRepo, never()).findById(any());
        verify(transactionRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found during buy")
    void testBuyUserNotFound() {
        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> transactionService.buy(buyRequest)
        );

        assertEquals("User not found", exception.getMessage());
        verify(assetRepo, times(1)).findBySymbol("AAPL");
        verify(userRepo, times(1)).findById(1L);
        verify(transactionRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully sell asset when holding exists with sufficient quantity")
    void testSellSuccess() {
        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));
        when(holdingsRepo.findByAsset(testAsset)).thenReturn(Optional.of(testHolding));
        when(transactionRepo.save(any(Transactions.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(holdingsRepo.save(any(Holdings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transactions result = transactionService.sell(sellRequest);

        assertNotNull(result);
        assertEquals(testAsset, result.getAsset());
        assertEquals(TransactionType.SELL, result.getTransactionType());
        assertEquals(new BigDecimal("30"), result.getQuantity());
        assertEquals(new BigDecimal("4800.00"), result.getMarketValue());

        verify(assetRepo, times(1)).findBySymbol("AAPL");
        verify(holdingsRepo, times(1)).findByAsset(testAsset);
        verify(transactionRepo, times(1)).save(any(Transactions.class));
        verify(holdingsRepo, times(1)).save(testHolding);
    }

    @Test
    @DisplayName("Should delete holding when selling all quantity")
    void testSellAllQuantity() {
        sellRequest.setQuantity(new BigDecimal("100"));

        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));
        when(holdingsRepo.findByAsset(testAsset)).thenReturn(Optional.of(testHolding));
        when(transactionRepo.save(any(Transactions.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transactions result = transactionService.sell(sellRequest);

        assertNotNull(result);
        verify(holdingsRepo, times(1)).delete(testHolding);
        verify(holdingsRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when asset not found during sell")
    void testSellAssetNotFound() {
        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> transactionService.sell(sellRequest)
        );

        assertEquals("Asset not found", exception.getMessage());
        verify(assetRepo, times(1)).findBySymbol("AAPL");
        verify(holdingsRepo, never()).findByAsset(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when holding not found during sell")
    void testSellHoldingNotFound() {
        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));
        when(holdingsRepo.findByAsset(testAsset)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> transactionService.sell(sellRequest)
        );

        assertEquals("Holding not found", exception.getMessage());
        verify(assetRepo, times(1)).findBySymbol("AAPL");
        verify(holdingsRepo, times(1)).findByAsset(testAsset);
    }

    @Test
    @DisplayName("Should throw RuntimeException when insufficient quantity to sell")
    void testSellInsufficientQuantity() {
        sellRequest.setQuantity(new BigDecimal("150"));

        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));
        when(holdingsRepo.findByAsset(testAsset)).thenReturn(Optional.of(testHolding));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> transactionService.sell(sellRequest)
        );

        assertEquals("Not enough quantity to sell", exception.getMessage());
        verify(assetRepo, times(1)).findBySymbol("AAPL");
        verify(holdingsRepo, times(1)).findByAsset(testAsset);
        verify(transactionRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should return all transactions when getAllTransactions is called")
    void testGetAllTransactions() {
        Transactions transaction1 = new Transactions();
        ReflectionTestUtils.setField(transaction1, "id", 1L);
        Transactions transaction2 = new Transactions();
        ReflectionTestUtils.setField(transaction2, "id", 2L);

        List<Transactions> expectedTransactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepo.findAll()).thenReturn(expectedTransactions);

        List<Transactions> actualTransactions = transactionService.getAllTransactions();

        assertEquals(expectedTransactions, actualTransactions);
        verify(transactionRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should calculate transaction summary correctly")
    void testGetSummary() {
        Transactions buyTx1 = new Transactions();
        buyTx1.setTransactionType(TransactionType.BUY);
        buyTx1.setTotalCost(new BigDecimal("10000.00"));
        buyTx1.setFees(new BigDecimal("50.00"));

        Transactions buyTx2 = new Transactions();
        buyTx2.setTransactionType(TransactionType.BUY);
        buyTx2.setTotalCost(new BigDecimal("5000.00"));
        buyTx2.setFees(new BigDecimal("25.00"));

        Transactions sellTx1 = new Transactions();
        sellTx1.setTransactionType(TransactionType.SELL);
        sellTx1.setMarketValue(new BigDecimal("12000.00"));

        Transactions sellTx2 = new Transactions();
        sellTx2.setTransactionType(TransactionType.SELL);
        sellTx2.setMarketValue(new BigDecimal("8000.00"));

        List<Transactions> allTransactions = Arrays.asList(buyTx1, buyTx2, sellTx1, sellTx2);
        when(transactionRepo.findAll()).thenReturn(allTransactions);

        TransactionSummaryDto summary = transactionService.getSummary();

        assertEquals(new BigDecimal("15000.00"), summary.getTotalInvested());
        assertEquals(new BigDecimal("5000.00"), summary.getTotalProfit());
        assertEquals(new BigDecimal("75.00"), summary.getTotalFees());
        assertEquals(new BigDecimal("750.00"), summary.getEstimatedTax());
        assertEquals(new BigDecimal("4175.00"), summary.getNetProfit());

        verify(transactionRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle zero profit case in summary calculation")
    void testGetSummaryZeroProfit() {
        Transactions buyTx = new Transactions();
        buyTx.setTransactionType(TransactionType.BUY);
        buyTx.setTotalCost(new BigDecimal("10000.00"));
        buyTx.setFees(new BigDecimal("50.00"));

        Transactions sellTx = new Transactions();
        sellTx.setTransactionType(TransactionType.SELL);
        sellTx.setMarketValue(new BigDecimal("10000.00"));

        List<Transactions> allTransactions = Arrays.asList(buyTx, sellTx);
        when(transactionRepo.findAll()).thenReturn(allTransactions);

        TransactionSummaryDto summary = transactionService.getSummary();

        assertEquals(new BigDecimal("10000.00"), summary.getTotalInvested());
        assertEquals(0, summary.getTotalProfit().compareTo(BigDecimal.ZERO));
        assertEquals(new BigDecimal("50.00"), summary.getTotalFees());
        assertEquals(0, summary.getEstimatedTax().compareTo(BigDecimal.ZERO));
        assertEquals(new BigDecimal("-50.00"), summary.getNetProfit());

        verify(transactionRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle negative profit case in summary calculation")
    void testGetSummaryNegativeProfit() {
        Transactions buyTx = new Transactions();
        buyTx.setTransactionType(TransactionType.BUY);
        buyTx.setTotalCost(new BigDecimal("10000.00"));
        buyTx.setFees(new BigDecimal("50.00"));

        Transactions sellTx = new Transactions();
        sellTx.setTransactionType(TransactionType.SELL);
        sellTx.setMarketValue(new BigDecimal("8000.00"));

        List<Transactions> allTransactions = Arrays.asList(buyTx, sellTx);
        when(transactionRepo.findAll()).thenReturn(allTransactions);

        TransactionSummaryDto summary = transactionService.getSummary();

        assertEquals(new BigDecimal("10000.00"), summary.getTotalInvested());
        assertEquals(new BigDecimal("-2000.00"), summary.getTotalProfit());
        assertEquals(new BigDecimal("50.00"), summary.getTotalFees());
        assertEquals(new BigDecimal("0"), summary.getEstimatedTax());
        assertEquals(new BigDecimal("-2050.00"), summary.getNetProfit());

        verify(transactionRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly in calculations")
    void testBigDecimalPrecision() {
        buyRequest.setQuantity(new BigDecimal("123.456789"));
        buyRequest.setPricePerUnit(new BigDecimal("123.456789"));
        buyRequest.setTotalCost(new BigDecimal("15241.578901"));

        when(assetRepo.findBySymbol("AAPL")).thenReturn(Optional.of(testAsset));
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(holdingsRepo.findByAsset(testAsset)).thenReturn(Optional.empty());
        when(transactionRepo.save(any(Transactions.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(holdingsRepo.save(any(Holdings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transactions result = transactionService.buy(buyRequest);

        assertEquals(new BigDecimal("123.456789"), result.getQuantity());
        assertEquals(new BigDecimal("123.456789"), result.getPricePerUnit());
        assertEquals(new BigDecimal("15241.578901"), result.getTotalCost());
    }
}
