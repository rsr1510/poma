const API_URL = "http://127.0.0.1:8000/prices";
const HOLDINGS_API = "http://127.0.0.1:8080/api/holdings";
const REFRESH_INTERVAL = 15000;

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
