const API_URL = "http://127.0.0.1:8000/prices";
const HOLDINGS_API = "http://127.0.0.1:8080/api/holdings";
const REFRESH_INTERVAL = 15000;

let topStocks = [
  { "name": "Reliance Industries", "symbol": "RELIANCE.NS" },
  { "name": "Tata Consultancy Services", "symbol": "TCS.NS" },
  { "name": "Infosys", "symbol": "INFY.NS" },
  { "name": "HDFC Bank", "symbol": "HDFCBANK.NS" },
  { "name": "ICICI Bank", "symbol": "ICICIBANK.NS" },
  { "name": "Bharti Airtel", "symbol": "BHARTIARTL.NS" },
  { "name": "State Bank of India", "symbol": "SBIN.NS" },
  { "name": "Kotak Mahindra Bank", "symbol": "KOTAKBANK.NS" },
  { "name": "Axis Bank", "symbol": "AXISBANK.NS" },
  { "name": "Larsen & Toubro", "symbol": "LT.NS" },
  { "name": "ITC", "symbol": "ITC.NS" },
  { "name": "Hindustan Unilever", "symbol": "HINDUNILVR.NS" },
  { "name": "Tata Steel", "symbol": "TATASTEEL.NS" },
  { "name": "NTPC", "symbol": "NTPC.NS" },
  { "name": "Power Grid Corporation", "symbol": "POWERGRID.NS" },
  { "name": "Coal India", "symbol": "COALINDIA.NS" },
  { "name": "Oil and Natural Gas Corporation", "symbol": "ONGC.NS" },
  { "name": "GAIL (India)", "symbol": "GAIL.NS" },
  { "name": "Bharat Petroleum Corporation", "symbol": "BPCL.NS" },
  { "name": "Hindustan Petroleum Corporation", "symbol": "HINDPETRO.NS" },
  { "name": "Tata Motors", "symbol": "TATAMOTORS.NS" },
  { "name": "Mahindra & Mahindra", "symbol": "M&M.NS" },
  { "name": "Maruti Suzuki India", "symbol": "MARUTI.NS" },
  { "name": "Bajaj Auto", "symbol": "BAJAJ-AUTO.NS" },
  { "name": "Hero MotoCorp", "symbol": "HEROMOTOCO.NS" },
  { "name": "Eicher Motors", "symbol": "EICHERMOT.NS" },
  { "name": "UltraTech Cement", "symbol": "ULTRACEMCO.NS" },
  { "name": "Shree Cement", "symbol": "SHREECEM.NS" },
  { "name": "Grasim Industries", "symbol": "GRASIM.NS" },
  { "name": "Ambuja Cements", "symbol": "AMBUJACEM.NS" },
  { "name": "JSW Steel", "symbol": "JSWSTEEL.NS" },
  { "name": "Tata Power", "symbol": "TATAPOWER.NS" },
  { "name": "Adani Ports and Special Economic Zone", "symbol": "ADANIPORTS.NS" },
  { "name": "Adani Enterprises", "symbol": "ADANIENT.NS" }
];
let topStocksLoaded = true;

let topCryptos = [
  { "name": "XRP USD", "symbol": "XRP-USD" },
  { "name": "USD Coin USD", "symbol": "USDC-USD" },
  { "name": "Solana USD", "symbol": "SOL-USD" },
  { "name": "TRON USD", "symbol": "TRX-USD" },
  { "name": "Wrapped TRON USD", "symbol": "WTRX-USD" },
  { "name": "Lido Staked ETH USD", "symbol": "STETH-USD" },
  { "name": "Dogecoin USD", "symbol": "DOGE-USD" },
  { "name": "Cardano USD", "symbol": "ADA-USD" },
  { "name": "Bitcoin Cash USD", "symbol": "BCH-USD" },
  { "name": "Lido wstETH USD", "symbol": "WSTETH-USD" },
  { "name": "Hyperliquid USD", "symbol": "HYPE32196-USD" },
  { "name": "USDS USD", "symbol": "USDS33039-USD" }
];

let topBonds = [
  { "name": "13 WEEK TREASURY BILL", "symbol": "^IRX" },
  { "name": "Treasury Yield 5 Years", "symbol": "^FVX" },
  { "name": "CBOE Interest Rate 10 Year T No", "symbol": "^TNX" },
  { "name": "Treasury Yield 30 Years", "symbol": "^TYX" },
  { "name": "2-Year T-Note Futures", "symbol": "2YY=F" },
  { "name": "10-Year T-Note Futures", "symbol": "ZN=F" }
];


const feesData = {
  stocks: [
    {
      platform: "Zerodha",
      allInPercent: 0.35,
      breakdown: [
        "₹0 brokerage",
        "STT (0.1%)",
        "Exchange & SEBI charges",
        "GST on charges",
        "Stamp duty"
      ]
    },
    {
      platform: "Groww",
      allInPercent: 0.35,
      breakdown: [
        "₹0 brokerage",
        "STT (0.1%)",
        "Exchange & SEBI charges",
        "GST on charges",
        "Stamp duty"
      ]
    },
    {
      platform: "Upstox",
      allInPercent: 0.40,
      breakdown: [
        "Brokerage (₹20 or %)",
        "STT (0.1%)",
        "Exchange & SEBI charges",
        "GST on charges",
        "Stamp duty"
      ]
    }
  ],

  bonds: [
    {
      platform: "Zerodha",
      allInPercent: 0.10,
      breakdown: [
        "Platform fee",
        "GST (18%)"
      ]
    },
    {
      platform: "Groww",
      allInPercent: 0.10,
      breakdown: [
        "Platform fee",
        "GST (18%)"
      ]
    },
    {
      platform: "5paisa",
      allInPercent: 0.08,
      breakdown: [
        "Flat brokerage",
        "Exchange charges",
        "GST (18%)"
      ]
    }
  ],

  crypto: [
    {
      platform: "CoinSwitch",
      allInPercent: 1.50,
      breakdown: [
        "Trading fee",
        "GST (18%)",
        "1% TDS"
      ]
    },
    {
      platform: "WazirX",
      allInPercent: 0.20,
      breakdown: [
        "Trading fee",
        "GST (18%)",
        "1% TDS"
      ]
    },
    {
      platform: "ZebPay",
      allInPercent: 1.80,
      breakdown: [
        "Trading fee",
        "GST (18%)",
        "1% TDS"
      ]
    }
  ]
};

/* ============================= */
/* Utility: Format INR Currency */
/* ============================= */
function formatINR(value) {
  return value.toLocaleString("en-IN", {
    style: "currency",
    currency: "INR",
  });
}

/* ============================= */
/* Load Holdings from Database */
/* ============================= */
async function loadHoldings() {
  try {
    const res = await fetch(HOLDINGS_API);
    const holdings = await res.json();

    const tbody = document.getElementById("holdingsBody");
    tbody.innerHTML = "";

    holdings.forEach((h) => {
      const symbol = h.asset.symbol;

      const row = document.createElement("tr");
      row.setAttribute("data-symbol", symbol);

      row.innerHTML = `
        <td>${symbol}</td>
        <td>${h.asset.type}</td>
        <td>${h.quantity}</td>
        <td>${h.avgBuyPrice}</td>
        <td class="current-price">Loading...</td>
        <td class="market-value">Loading...</td>
        <td class="pl">Loading...</td>
      `;

      tbody.appendChild(row);
    });

    updatePrices();
  } catch (err) {
    console.error("Error loading holdings:", err);
  }
}

/* ============================= */
/* Fetch Live Prices + Update Table */
/* ============================= */
async function updatePrices() {
  const rows = document.querySelectorAll("#holdingsBody tr");
  const symbols = Array.from(rows).map((r) => r.getAttribute("data-symbol"));

  if (symbols.length === 0) return;

  try {
    const res = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(symbols),
    });

    const data = await res.json();

    rows.forEach((row) => {
      const symbol = row.getAttribute("data-symbol");
      const currentPrice = data[symbol]?.price;

      const units = parseFloat(row.children[2].innerText);
      const buyPrice = parseFloat(row.children[3].innerText);

      if (currentPrice) {
        const marketValue = currentPrice * units;
        const pl = marketValue - buyPrice * units;

        // Update Table Values
        row.querySelector(".current-price").innerText = formatINR(currentPrice);
        row.querySelector(".market-value").innerText = formatINR(marketValue);

        const plCell = row.querySelector(".pl");
        plCell.innerText = formatINR(pl);

        // Profit / Loss Coloring
        if (pl >= 0) {
          plCell.classList.add("positive");
          plCell.classList.remove("negative");
        } else {
          plCell.classList.add("negative");
          plCell.classList.remove("positive");
        }
      }
    });

    // Update Dashboard After Prices Update
    updateDashboardTotals();
    updatePortfolioSummary();
    updateBestWorstAssets();
    updateDailyChange();
  } catch (err) {
    console.error("Error fetching prices:", err);
  }
}

/* ============================= */
/* Dashboard Totals by Asset Type */
/* ============================= */
function updateDashboardTotals() {
  const totals = {
    STOCK: 0,
    BOND: 0,
    CRYPTO: 0,
    CASH: 0,
  };

  document.querySelectorAll("#holdingsBody tr").forEach((row) => {
    const type = row.children[1].innerText;
    const plText = row.querySelector(".pl").innerText.replace(/₹|,/g, "");
    const pl = parseFloat(plText);

    if (!isNaN(pl)) {
      totals[type] += pl;
    }
  });

  document.getElementById("stocksStat").innerHTML =
    `${formatINR(totals.STOCK)}<span>Stocks</span>`;

  document.getElementById("bondsStat").innerHTML =
    `${formatINR(totals.BOND)}<span>Bonds</span>`;

  document.getElementById("cryptoStat").innerHTML =
    `${formatINR(totals.CRYPTO)}<span>Crypto</span>`;

  document.getElementById("cashStat").innerHTML =
    `${formatINR(totals.CASH)}<span>Cash</span>`;
}

/* ============================= */
/* Portfolio Total Value + Return */
/* ============================= */
function updatePortfolioSummary() {
  let totalMarketValue = 0;
  let totalInvested = 0;

  document.querySelectorAll("#holdingsBody tr").forEach((row) => {
    const units = parseFloat(row.children[2].innerText);
    const buyPrice = parseFloat(row.children[3].innerText);

    const currentText = row
      .querySelector(".current-price")
      .innerText.replace(/₹|,/g, "");
    const currentPrice = parseFloat(currentText);

    if (!isNaN(currentPrice)) {
      totalInvested += buyPrice * units;
      totalMarketValue += currentPrice * units;
    }
  });

  const totalProfit = totalMarketValue - totalInvested;

  const returnPercent =
    totalInvested > 0 ? (totalProfit / totalInvested) * 100 : 0;

  document.getElementById("portfolioValue").innerText =
    formatINR(totalMarketValue);

  const returnEl = document.getElementById("portfolioReturn");
  returnEl.innerText =
    `${returnPercent >= 0 ? "+" : ""}${returnPercent.toFixed(2)}%`;

  // Color Return
  returnEl.className = returnPercent >= 0 ? "positive" : "negative";
}

/* ============================= */
/* Best + Worst Performing Asset */
/* ============================= */
function updateBestWorstAssets() {
  let best = { symbol: "-", profit: -Infinity };
  let worst = { symbol: "-", profit: Infinity };

  document.querySelectorAll("#holdingsBody tr").forEach((row) => {
    const symbol = row.children[0].innerText;

    const plText = row.querySelector(".pl").innerText.replace(/₹|,/g, "");
    const profit = parseFloat(plText);

    if (!isNaN(profit)) {
      if (profit > best.profit) best = { symbol, profit };
      if (profit < worst.profit) worst = { symbol, profit };
    }
  });

  document.getElementById("bestAsset").innerHTML = `
    <span>Best Performer</span>
    <div class="value">${best.symbol}</div>
    <strong class="positive">${formatINR(best.profit)}</strong>
  `;

  document.getElementById("worstAsset").innerHTML = `
    <span>Worst Performer</span>
    <div class="value">${worst.symbol}</div>
    <strong class="negative">${formatINR(worst.profit)}</strong>
  `;
}

/* ============================= */
/* Daily Change (Total P/L Today) */
/* ============================= */
function updateDailyChange() {
  let totalPL = 0;

  document.querySelectorAll("#holdingsBody tr").forEach((row) => {
    const plText = row.querySelector(".pl").innerText.replace(/₹|,/g, "");
    const profit = parseFloat(plText);

    if (!isNaN(profit)) totalPL += profit;
  });

  document.getElementById("dailyChange").innerHTML = `
    <span>Daily Change</span>
    <div class="value">${formatINR(totalPL)}</div>
  `;

  document.getElementById("dailyChange").className =
    totalPL >= 0 ? "stat positive" : "stat negative";
}

window.addEventListener("load", () => {
  loadHoldings();
});

setInterval(updatePrices, REFRESH_INTERVAL);
function openAssetModal() {
  document.getElementById("assetModal").style.display = "flex";
}

function closeAssetModal() {
  document.getElementById("assetModal").style.display = "none";
}

function updateAssetOptions() {
  const type = document.getElementById("assetType").value;
  const category = document.getElementById("assetCategory");

  category.innerHTML = "";

  if (type === "cash") {
    category.innerHTML = `<option>INR Cash</option>`;
  }

  if (type === "stock") {
    category.innerHTML = `
      <option>Large Cap</option>
      <option>Mid Cap</option>
      <option>Small Cap</option>
    `;
  }

  if (type === "crypto") {
    category.innerHTML = `
      <option>Bitcoin</option>
      <option>Ethereum</option>
      <option>Altcoin</option>
    `;
  }
}

const tabs = document.querySelectorAll(".asset-tab");
const feesGrid = document.getElementById("feesGrid");

function renderFees(asset) {
  feesGrid.innerHTML = "";

  feesData[asset].forEach(item => {
    feesGrid.innerHTML += `
      <div class="fee-card">
        <h4>${item.platform}</h4>
        <span>${asset.toUpperCase()}</span>

        <div class="fee-percent">
          ~${item.allInPercent}%
        </div>

        <div class="fee-breakdown">
          ${item.breakdown.map(b => `• ${b}`).join("<br>")}
        </div>
      </div>
    `;
  });
}

tabs.forEach(tab => {
  tab.addEventListener("click", () => {
    tabs.forEach(t => t.classList.remove("active"));
    tab.classList.add("active");
    renderFees(tab.dataset.asset);
  });
});

// initial load
renderFees("stocks");

// Navigation
const navButtons = document.querySelectorAll(".nav");
const views = document.querySelectorAll(".view");

navButtons.forEach(button => {
  button.addEventListener("click", () => {
    // Remove active from all nav and views
    navButtons.forEach(btn => btn.classList.remove("active"));
    views.forEach(view => view.classList.remove("active"));

    // Add active to clicked nav and corresponding view
    button.classList.add("active");
    const viewId = button.dataset.view + "-view";
    document.getElementById(viewId).classList.add("active");
  });
});

let currentFeePercent = 0;

function updateFees() {
  const assetType = document.getElementById("assetType").value;
  const platform = document.getElementById("platform").value;

  let assetKey = assetType;
  if (assetType === "stock") assetKey = "stocks";
  if (assetType === "cash") {
    currentFeePercent = 0;
    document.getElementById("platformFeeDetails").innerHTML = "No fees for cash";
    updateBill();
    return;
  }

  const data = feesData[assetKey];
  if (data) {
    const platformData = data.find(p => p.platform.toLowerCase() === platform);
    if (platformData) {
      currentFeePercent = platformData.allInPercent;
      document.getElementById("platformFeeDetails").innerHTML = `
        <div class="fee-percent">~${platformData.allInPercent}%</div>
        <div class="fee-breakdown">${platformData.breakdown.map(b => `• ${b}`).join("<br>")}</div>
      `;
    } else {
      currentFeePercent = 0;
      document.getElementById("platformFeeDetails").innerHTML = "No fees data available for this platform";
    }
  } else {
    currentFeePercent = 0;
    document.getElementById("platformFeeDetails").innerHTML = "Select an asset type and platform";
  }
  updateBill();
}

function updateBill() {
  const units = parseFloat(document.getElementById("units").value) || 0;
  const pricePerUnit = parseFloat(document.getElementById("pricePerUnit").value) || 0;

  const totalInvestment = units * pricePerUnit;
  const fees = totalInvestment * (currentFeePercent / 100);
  const totalCost = totalInvestment + fees;

  document.getElementById("totalInvestment").innerText = formatINR(totalInvestment);
  document.getElementById("fees").innerText = formatINR(fees);
  document.getElementById("totalCost").innerText = formatINR(totalCost);
}

function updateAssetOptions() {
  const type = document.getElementById("assetType").value;
  const category = document.getElementById("assetCategory");
  const platform = document.getElementById("platform");

  category.innerHTML = "";
  platform.innerHTML = "";

  if (type === "cash") {
    category.innerHTML = `<option>INR Cash</option>`;
    platform.innerHTML = `<option value="none">No Platform</option>`;
  }

  if (type === "stock") {
    category.innerHTML = topStocks.map(stock => `<option value="${stock.symbol}">${stock.name}</option>`).join('');
    platform.innerHTML = `
      <option value="zerodha">Zerodha</option>
      <option value="groww">Groww</option>
      <option value="upstox">Upstox</option>
    `;
  }

  if (type === "crypto") {
    category.innerHTML = topCryptos.map(crypto => `<option value="${crypto.symbol}">${crypto.name}</option>`).join('');
    platform.innerHTML = `
      <option value="coinswitch">CoinSwitch</option>
      <option value="wazirx">WazirX</option>
      <option value="zebpay">ZebPay</option>
    `;
  }

  if (type === "bonds") {
    category.innerHTML = topBonds.map(bond => `<option value="${bond.symbol}">${bond.name}</option>`).join('');
    platform.innerHTML = `
      <option value="zerodha">Zerodha</option>
      <option value="groww">Groww</option>
      <option value="5paisa">5paisa</option>
    `;
  }

  updateFees();
}


