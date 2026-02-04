const API_URL = "http://127.0.0.1:8080/prices";
const HOLDINGS_API = "http://127.0.0.1:8080/api/holdings";
const REFRESH_INTERVAL = 15000;

// Load Holdings from DB
async function loadHoldings() {
  const res = await fetch(HOLDINGS_API);
  const holdings = await res.json();

  const tbody = document.getElementById("holdingsBody");
  tbody.innerHTML = "";

  holdings.forEach(h => {
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
}

// Fetch Prices from Backend
async function updatePrices() {
  const rows = document.querySelectorAll("#holdingsBody tr");
  const symbols = Array.from(rows).map(r => r.getAttribute("data-symbol"));

  if (symbols.length === 0) return;

  const res = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(symbols),
  });

  const data = await res.json();

  rows.forEach(row => {
    const symbol = row.getAttribute("data-symbol");
    const currentPrice = data[symbol]?.price;

    const units = parseFloat(row.children[2].innerText);
    const buyPrice = parseFloat(row.children[3].innerText);

    if (currentPrice) {
      const marketValue = currentPrice * units;
      const pl = marketValue - buyPrice * units;

      row.querySelector(".current-price").innerText = `₹${currentPrice}`;
      row.querySelector(".market-value").innerText = `₹${marketValue.toFixed(2)}`;
      row.querySelector(".pl").innerText = `₹${pl.toFixed(2)}`;
    }
  });
}

// Load holdings once on startup
window.addEventListener("load", loadHoldings);

// Refresh prices every 15 sec
setInterval(updatePrices, REFRESH_INTERVAL);
