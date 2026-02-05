const API_URL = "http://127.0.0.1:8000/prices";
const REFRESH_INTERVAL = 15000;

async function updateDashboard() {
    const rows = document.querySelectorAll('#holdingsTable tbody tr');
    const symbols = Array.from(rows).map(row => row.getAttribute('data-symbol'));

    try {
        const res = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(symbols),
        });
        const priceData = await res.json();

        // Variables to track portfolio-wide totals
        let totalInvestment = 0;
        let totalCurrentValue = 0;
        
        // Dictionary to track totals per asset type
        const typeTotals = {
            "Stocks": 0,
            "Bonds": 0,
            "Crypto": 0,
            "Cash": 0
        };

        rows.forEach(row => {
            const symbol = row.getAttribute('data-symbol');
            const type = row.getAttribute('data-type');
            const units = parseFloat(row.cells[2].innerText);
            const buyPrice = parseFloat(row.cells[3].innerText);
            const currentPrice = priceData[symbol]?.price || 0;

            if (currentPrice > 0) {
                const marketValue = currentPrice * units;
                const investment = buyPrice * units;
                const pl = marketValue - investment;

                // Update Row UI
                row.querySelector('.current-price').innerText = `₹${currentPrice.toLocaleString()}`;
                row.querySelector('.market-value').innerText = `₹${marketValue.toLocaleString()}`;
                
                const plCell = row.querySelector('.pl');
                plCell.innerText = `₹${pl.toLocaleString()}`;
                plCell.className = pl >= 0 ? 'pl positive' : 'pl negative';

                // Accumulate Globals
                totalInvestment += investment;
                totalCurrentValue += marketValue;
                
                // Accumulate Type Totals
                if (typeTotals.hasOwnProperty(type)) {
                    typeTotals[type] += marketValue;
                }
            }
        });

        updateSummaryUI(totalInvestment, totalCurrentValue, typeTotals);

    } catch (err) {
        console.error('Fetch Error:', err);
    }
}

function updateSummaryUI(totalInvested, totalCurrent, typeTotals) {
    // 1. Calculate Portfolio Value & Return %
    const totalPL = totalCurrent - totalInvested;
    const returnPercent = totalInvested > 0 ? (totalPL / totalInvested) * 100 : 0;

    // 2. Update Top Header
    const valueEl = document.getElementById('total-value');
    const returnEl = document.getElementById('total-return');

    valueEl.innerText = `₹${totalCurrent.toLocaleString('en-IN')}`;
    returnEl.innerText = `${returnPercent >= 0 ? '+' : ''}${returnPercent.toFixed(2)}%`;
    
    // UI Color Logic
    returnEl.className = returnPercent >= 0 ? 'positive' : 'negative';

    // 3. Update Bottom Asset Stats
    for (const [type, value] of Object.entries(typeTotals)) {
        const statEl = document.getElementById(`val-${type}`);
        if (statEl) {
            statEl.innerText = `₹${value.toLocaleString('en-IN')}`;
        }
    }
}

// Initial Load & Interval
window.addEventListener('load', updateDashboard);
setInterval(updateDashboard, REFRESH_INTERVAL);