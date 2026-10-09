/**
 * Sim Trade — TradingView Style Institutional Trading Terminal Engine
 */

document.addEventListener('DOMContentLoaded', () => {
    initClock();
    initTradingViewChart();
    initWatchlistDelegation();
    startPriceTickerSimulator();
});

/* ==========================================================================
   1. Live Indian Standard Time (IST) Clock
   ========================================================================== */
function initClock() {
    const clockEl = document.getElementById('currentTimeDisplay');
    if (!clockEl) return;

    function update() {
        const now = new Date();
        const options = {
            timeZone: 'Asia/Kolkata',
            hour12: false,
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        };
        try {
            clockEl.textContent = now.toLocaleTimeString('en-IN', options);
        } catch (e) {
            clockEl.textContent = now.toTimeString().split(' ')[0];
        }
    }

    update();
    setInterval(update, 1000);
}

/* ==========================================================================
   2. TradingView Interactive Candlestick / Area Chart Engine
   ========================================================================== */
let chartState = {
    canvas: null,
    ctx: null,
    timeframe: '1D',
    type: 'candles', // 'candles', 'area', 'line'
    candles: [],
    hoverIndex: -1,
    mouseX: -1,
    mouseY: -1
};

function generateRealisticCandles(basePrice, count = 100) {
    const candles = [];
    let current = basePrice * 0.95;
    const now = new Date();

    for (let i = count; i >= 0; i--) {
        const time = new Date(now.getTime() - i * 5 * 60000); 
        const volatility = current * 0.005;
        const open = current;
        const delta = (Math.random() - 0.48) * volatility;
        const close = open + delta;
        const high = Math.max(open, close) + Math.random() * (volatility * 0.5);
        const low = Math.min(open, close) - Math.random() * (volatility * 0.5);
        const volume = Math.floor(Math.random() * 40000 + 5000);

        candles.push({
            time: time.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: false }),
            open: Math.round(open * 100) / 100,
            high: Math.round(high * 100) / 100,
            low: Math.round(low * 100) / 100,
            close: Math.round(close * 100) / 100,
            volume
        });
        current = close;
    }

    if (candles.length > 0) {
        candles[candles.length - 1].close = basePrice;
    }
    return candles;
}

function initTradingViewChart() {
    const canvas = document.getElementById('tradingViewCanvas');
    if (!canvas) return;

    chartState.canvas = canvas;
    chartState.ctx = canvas.getContext('2d');
    chartState.candles = generateRealisticCandles(activeStock.price);

    function resize() {
        const wrapper = document.getElementById('chartWrapper');
        if (!wrapper) return;
        const rect = wrapper.getBoundingClientRect();
        canvas.width = rect.width * window.devicePixelRatio;
        canvas.height = rect.height * window.devicePixelRatio;
        chartState.ctx.scale(window.devicePixelRatio, window.devicePixelRatio);
        drawChart();
    }

    window.addEventListener('resize', resize);
    // Slight delay to ensure DOM is fully rendered before measuring
    setTimeout(resize, 50);

    canvas.addEventListener('mousemove', (e) => {
        const rect = canvas.getBoundingClientRect();
        chartState.mouseX = e.clientX - rect.left;
        chartState.mouseY = e.clientY - rect.top;

        const candleWidth = rect.width / chartState.candles.length;
        chartState.hoverIndex = Math.min(
            chartState.candles.length - 1,
            Math.max(0, Math.floor(chartState.mouseX / candleWidth))
        );

        updateOHLCBar(chartState.candles[chartState.hoverIndex]);
        drawChart();
    });

    canvas.addEventListener('mouseleave', () => {
        chartState.mouseX = -1;
        chartState.mouseY = -1;
        chartState.hoverIndex = -1;
        if (chartState.candles.length > 0) {
            updateOHLCBar(chartState.candles[chartState.candles.length - 1]);
        }
        drawChart();
    });

    // Timeframe switcher
    const tfBtns = document.querySelectorAll('.axis-tf-btn');
    tfBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            tfBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            chartState.timeframe = btn.textContent;
            chartState.candles = generateRealisticCandles(activeStock.price, chartState.timeframe === '1D' ? 45 : 100);
            drawChart();
        });
    });

    if (chartState.candles.length > 0) {
        updateOHLCBar(chartState.candles[chartState.candles.length - 1]);
    }
}

function updateOHLCBar(c) {
    if (!c) return;
    const oEl = document.getElementById('legO');
    const hEl = document.getElementById('legH');
    const lEl = document.getElementById('legL');
    const cEl = document.getElementById('legC');
    
    if (oEl) oEl.textContent = c.open.toFixed(2);
    if (hEl) hEl.textContent = c.high.toFixed(2);
    if (lEl) lEl.textContent = c.low.toFixed(2);
    if (cEl) cEl.textContent = c.close.toFixed(2);
}

function drawChart() {
    const { canvas, ctx, candles, type, mouseX, mouseY } = chartState;
    if (!canvas || !ctx || candles.length === 0) return;

    const width = canvas.width / window.devicePixelRatio;
    const height = canvas.height / window.devicePixelRatio;

    ctx.clearRect(0, 0, width, height);

    let minPrice = Infinity;
    let maxPrice = -Infinity;
    let maxVol = 0;

    candles.forEach(c => {
        if (c.low < minPrice) minPrice = c.low;
        if (c.high > maxPrice) maxPrice = c.high;
        if (c.volume > maxVol) maxVol = c.volume;
    });

    const pricePadding = (maxPrice - minPrice) * 0.1 || 1.0;
    minPrice -= pricePadding;
    maxPrice += pricePadding;
    const priceRange = maxPrice - minPrice;

    const chartHeight = height * 0.78;
    const volHeight = height * 0.20;
    const rightAxisWidth = 65;
    const plotWidth = width - rightAxisWidth;
    const candleWidth = plotWidth / candles.length;

    function priceToY(price) {
        return chartHeight - ((price - minPrice) / priceRange) * chartHeight;
    }

    // 1. Grid Lines
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.05)';
    ctx.lineWidth = 1;
    ctx.setLineDash([4, 4]);

    const gridSteps = 5;
    for (let i = 0; i <= gridSteps; i++) {
        const y = (chartHeight / gridSteps) * i;
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(plotWidth, y);
        ctx.stroke();

        const priceAtY = maxPrice - (i / gridSteps) * priceRange;
        ctx.setLineDash([]);
        ctx.fillStyle = '#787b86';
        ctx.font = '11px JetBrains Mono, monospace';
        ctx.textAlign = 'left';
        ctx.fillText(priceAtY.toFixed(1), plotWidth + 6, y + 4);
        ctx.setLineDash([4, 4]);
    }
    ctx.setLineDash([]);

    // 2. Volume
    candles.forEach((c, i) => {
        const x = i * candleWidth;
        const vH = (c.volume / (maxVol || 1)) * volHeight;
        const y = height - vH;
        ctx.fillStyle = c.close >= c.open ? 'rgba(8, 153, 129, 0.25)' : 'rgba(242, 54, 69, 0.25)';
        ctx.fillRect(x + 1, y, Math.max(1, candleWidth - 2), vH);
    });

    // 3. Candlesticks
    candles.forEach((c, i) => {
        const x = i * candleWidth + candleWidth / 2;
        const openY = priceToY(c.open);
        const closeY = priceToY(c.close);
        const highY = priceToY(c.high);
        const lowY = priceToY(c.low);

        const isBull = c.close >= c.open;
        const color = isBull ? '#089981' : '#f23645';

        ctx.strokeStyle = color;
        ctx.lineWidth = 1.2;
        ctx.beginPath();
        ctx.moveTo(x, highY);
        ctx.lineTo(x, lowY);
        ctx.stroke();

        const bodyTop = Math.min(openY, closeY);
        const bodyHeight = Math.max(2, Math.abs(closeY - openY));
        const bodyWidth = Math.max(2, candleWidth * 0.7);

        ctx.fillStyle = color;
        ctx.fillRect(x - bodyWidth / 2, bodyTop, bodyWidth, bodyHeight);
    });

    // 4. Crosshair
    if (mouseX >= 0 && mouseX <= plotWidth && mouseY >= 0 && mouseY <= height) {
        ctx.strokeStyle = 'rgba(255, 255, 255, 0.3)';
        ctx.lineWidth = 1;
        ctx.setLineDash([4, 4]);

        ctx.beginPath();
        ctx.moveTo(0, mouseY);
        ctx.lineTo(plotWidth, mouseY);
        ctx.stroke();

        ctx.beginPath();
        ctx.moveTo(mouseX, 0);
        ctx.lineTo(mouseX, height);
        ctx.stroke();
        ctx.setLineDash([]);

        const hoverPrice = maxPrice - (mouseY / chartHeight) * priceRange;
        if (mouseY <= chartHeight) {
            ctx.fillStyle = '#2962ff';
            ctx.fillRect(plotWidth, mouseY - 10, rightAxisWidth, 20);
            ctx.fillStyle = '#ffffff';
            ctx.font = 'bold 11px JetBrains Mono, monospace';
            ctx.textAlign = 'left';
            ctx.fillText(hoverPrice.toFixed(2), plotWidth + 4, mouseY + 4);
        }
    }
}

/* ==========================================================================
   3. Stock Selection & Interactive Delegation
   ========================================================================== */
let activeStock = {
    symbol: 'RELIANCE',
    name: 'Reliance Industries Ltd.',
    price: 2985.50
};

function selectStockForTrade(symbol, name, price, change, changep, high, low, open) {
    activeStock.symbol = symbol;
    activeStock.name = name || symbol;
    activeStock.price = parseFloat(price);

    const isBull = parseFloat(change) >= 0;
    const sign = isBull ? '+' : '';
    const textClass = isBull ? 'bull-text' : 'bear-text';

    // 1. Top Nav
    const navSym = document.getElementById('navActiveSymbol');
    if (navSym) navSym.textContent = symbol;

    // 2. Chart Legend
    const legSym = document.getElementById('chartLegendSymbol');
    const legOhlc = document.getElementById('chartLegendOhlc');
    const legChg = document.getElementById('legChg');
    const legChgP = document.getElementById('legChgP');
    
    if (legSym) legSym.textContent = symbol;
    if (legOhlc) {
        legOhlc.className = 'chart-legend-ohlc ' + (isBull ? 'bull' : 'bear');
    }
    if (legChg) legChg.textContent = sign + change;
    if (legChgP) legChgP.textContent = sign + changep + '%';

    // 3. Details Widget
    const detSym = document.getElementById('detSymbol');
    const detName = document.getElementById('detName');
    const detPrice = document.getElementById('detPrice');
    const detChange = document.getElementById('detChange');
    const detLow = document.getElementById('detLow');
    const detHigh = document.getElementById('detHigh');
    const detNewsSym = document.getElementById('detNewsSymbol');

    if (detSym) detSym.textContent = symbol;
    if (detName) detName.textContent = name + ' • NSE';
    if (detPrice) {
        detPrice.textContent = parseFloat(price).toFixed(2);
        detPrice.className = 'details-price ' + textClass;
    }
    if (detChange) {
        detChange.textContent = sign + change + ' (' + sign + changep + '%)';
        detChange.className = 'details-change ' + textClass;
    }
    if (detLow && low) detLow.textContent = parseFloat(low).toFixed(2);
    if (detHigh && high) detHigh.textContent = parseFloat(high).toFixed(2);
    if (detNewsSym) detNewsSym.textContent = symbol;

    // Re-generate chart candles
    if (chartState.canvas) {
        chartState.candles = generateRealisticCandles(activeStock.price);
        drawChart();
    }
}

function initWatchlistDelegation() {
    const watchlistRows = document.querySelectorAll('.stock-row');
    
    watchlistRows.forEach(row => {
        row.addEventListener('click', () => {
            // Update active state
            watchlistRows.forEach(r => r.classList.remove('active'));
            row.classList.add('active');

            const sym = row.dataset.symbol;
            const nm = row.dataset.name;
            const pr = row.dataset.price;
            const chg = row.dataset.change;
            const chgp = row.dataset.changep;
            const hi = row.dataset.high;
            const lo = row.dataset.low;
            const op = row.dataset.open;

            selectStockForTrade(sym, nm, pr, chg, chgp, hi, lo, op);
        });
    });
}

function startPriceTickerSimulator() {
    setInterval(() => {
        const rows = document.querySelectorAll('.stock-row');
        if (rows.length === 0) return;

        const randIdx = Math.floor(Math.random() * rows.length);
        const row = rows[randIdx];
        
        let currentPrice = parseFloat(row.dataset.price);
        if (!currentPrice || isNaN(currentPrice)) return;

        const pctDelta = (Math.random() * 0.004) - 0.002;
        const newPrice = Math.round((currentPrice * (1 + pctDelta)) * 100) / 100;
        
        row.dataset.price = newPrice.toFixed(2);

        const lastEl = row.querySelector('.wl-last');
        if (lastEl) {
            lastEl.textContent = newPrice.toFixed(2);
            // Flash color
            lastEl.style.backgroundColor = 'rgba(255,255,255,0.2)';
            setTimeout(() => lastEl.style.backgroundColor = 'transparent', 300);
        }

        // If it's active stock, pulse the chart as well
        if (row.classList.contains('active')) {
            activeStock.price = newPrice;
            const detPrice = document.getElementById('detPrice');
            if (detPrice) detPrice.textContent = newPrice.toFixed(2);
            
            if (chartState.candles.length > 0) {
                chartState.candles[chartState.candles.length - 1].close = newPrice;
                drawChart();
            }
        }
    }, 2000);
}
