package org.poma.jpa.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.poma.jpa.backend.dto.SellRequest;
import org.poma.jpa.backend.dto.TransactionRequest;
import org.poma.jpa.backend.dto.TransactionSummaryDto;
import org.poma.jpa.backend.entity.Assets;
import org.poma.jpa.backend.entity.Transactions;
import org.poma.jpa.backend.entity.TransactionType;
import org.poma.jpa.backend.service.TransactionService;
import org.poma.jpa.backend.exceptions.GlobalExceptionHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    private ObjectMapper objectMapper;

    private TransactionRequest buyRequest;
    private SellRequest sellRequest;
    private Transactions buyTransaction;
    private Transactions sellTransaction;
    private TransactionSummaryDto summaryDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        buyRequest = new TransactionRequest();
        buyRequest.setSymbol("AAPL");
        buyRequest.setQuantity(new BigDecimal("100"));
        buyRequest.setPricePerUnit(new BigDecimal("150.50"));
        buyRequest.setPlatform("Robinhood");
        buyRequest.setFees(new BigDecimal("5.00"));
        buyRequest.setTotalCost(new BigDecimal("15055.00"));

        sellRequest = new SellRequest();
        sellRequest.setSymbol("AAPL");
        sellRequest.setQuantity(new BigDecimal("50"));

        Assets testAsset = new Assets();
        ReflectionTestUtils.setField(testAsset, "id", 1L);
        testAsset.setSymbol("AAPL");
        testAsset.setName("Apple Inc.");

        buyTransaction = new Transactions();
        ReflectionTestUtils.setField(buyTransaction, "id", 1L);
        buyTransaction.setAsset(testAsset);
        buyTransaction.setTransactionType(TransactionType.BUY);
        buyTransaction.setQuantity(new BigDecimal("100"));
        buyTransaction.setPricePerUnit(new BigDecimal("150.50"));
        buyTransaction.setPlatform("Robinhood");
        buyTransaction.setFees(new BigDecimal("5.00"));
        buyTransaction.setTotalCost(new BigDecimal("15055.00"));

        sellTransaction = new Transactions();
        ReflectionTestUtils.setField(sellTransaction, "id", 2L);
        sellTransaction.setAsset(testAsset);
        sellTransaction.setTransactionType(TransactionType.SELL);
        sellTransaction.setQuantity(new BigDecimal("50"));
        sellTransaction.setMarketValue(new BigDecimal("7525.00"));

        summaryDto = new TransactionSummaryDto(
                new BigDecimal("30000.00"),
                new BigDecimal("5000.00"),
                new BigDecimal("150.00"),
                new BigDecimal("750.00"),
                new BigDecimal("4100.00")
        );
    }

    @Test
    @DisplayName("Should create buy transaction when POST /api/transactions/buy is called")
    void testBuyAsset() throws Exception {
        when(transactionService.buy(any(TransactionRequest.class))).thenReturn(buyTransaction);

        mockMvc.perform(post("/api/transactions/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.transactionType").value("BUY"))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.pricePerUnit").value(150.50))
                .andExpect(jsonPath("$.platform").value("Robinhood"))
                .andExpect(jsonPath("$.fees").value(5.00))
                .andExpect(jsonPath("$.totalCost").value(15055.00))
                .andExpect(jsonPath("$.asset.symbol").value("AAPL"));

        verify(transactionService, times(1)).buy(any(TransactionRequest.class));
    }

    @Test
    @DisplayName("Should handle service exceptions during buy transaction")
    void testBuyAssetServiceException() throws Exception {
        when(transactionService.buy(any(TransactionRequest.class)))
                .thenThrow(new RuntimeException("Asset not found"));

        mockMvc.perform(post("/api/transactions/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Asset not found")));

        verify(transactionService, times(1)).buy(any(TransactionRequest.class));
    }
}
