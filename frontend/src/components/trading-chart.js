import { createChart, CrosshairMode } from 'lightweight-charts';

/**
 * Institutional Interactive Financial Candlestick Chart.
 * Powered by TradingView lightweight-charts with SimTrade Warm Graphite + Copper + Jade theme.
 */
export class TradingChart extends HTMLElement {

    constructor() {
        super();
        this._symbol = '';
        this._companyName = '';
        this._currentTimeframe = '1m';
        this._height = 360;
        this._isCompact = false;

        this.chart = null;
        this.candlestickSeries = null;
        this.volumeSeries = null;
        this.resizeObserver = null;
        this.latestCandle = null;
        this.candleData = [];
        this._initialized = false;
    }

    get symbol() { return this._symbol; }
    set symbol(val) {
        this._symbol = val || '';
        if (this.symbolEl) this.symbolEl.textContent = this._symbol;
    }

    get companyName() { return this._companyName; }
    set companyName(val) {
        this._companyName = val || '';
        if (this.companyEl) this.companyEl.textContent = this._companyName;
    }

    get currentTimeframe() { return this._currentTimeframe; }
    set currentTimeframe(val) {
        this._currentTimeframe = val || '1m';
        this.updateTimeframeButtons();
    }

    get height() { return this._height; }
    set height(val) {
        this._height = Number(val) || 360;
        if (this.canvasContainer) {
            this.canvasContainer.style.height = this._height + 'px';
        }
        if (this.chart) {
            this.chart.applyOptions({ height: this._height });
        }
    }

    get isCompact() { return this._isCompact; }
    set isCompact(val) {
        this._isCompact = Boolean(val);
        if (this.cardEl) {
            if (this._isCompact) this.cardEl.classList.add('st-chart-compact');
            else this.cardEl.classList.remove('st-chart-compact');
        }
    }

    connectedCallback() {
        if (this._initialized) return;
        this._initialized = true;

        this.buildDOM();
        requestAnimationFrame(() => {
            this.initChart();
        });
    }

    disconnectedCallback() {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect();
            this.resizeObserver = null;
        }
        if (this.chart) {
            this.chart.remove();
            this.chart = null;
        }
    }

    buildDOM() {
        this.innerHTML = `
            <div class="st-chart-card ${this._isCompact ? 'st-chart-compact' : ''}">
                <div class="st-chart-header">
                    <div class="st-chart-title-area">
                        <span class="st-chart-symbol">${this._symbol}</span>
                        <span class="st-chart-company">${this._companyName}</span>
                        <span class="st-chart-badge-paper">PAPER SIMULATION</span>
                        <span class="st-chart-tz-badge">Asia/Kolkata (IST)</span>
                        <div class="st-chart-legend"></div>
                    </div>
                    <div class="st-chart-timeframes">
                        <button type="button" class="st-tf-btn ${this._currentTimeframe === '1m' ? 'active' : ''}" data-tf="1m">1m</button>
                        <button type="button" class="st-tf-btn ${this._currentTimeframe === '5m' ? 'active' : ''}" data-tf="5m">5m</button>
                        <button type="button" class="st-tf-btn ${this._currentTimeframe === '15m' ? 'active' : ''}" data-tf="15m">15m</button>
                        <button type="button" class="st-tf-btn ${this._currentTimeframe === '1h' ? 'active' : ''}" data-tf="1h">1h</button>
                        <button type="button" class="st-tf-btn ${this._currentTimeframe === '1d' ? 'active' : ''}" data-tf="1d">1d</button>
                    </div>
                </div>
                <div class="st-chart-canvas-container" style="height: ${this._height}px;"></div>
            </div>
        `;

        this.cardEl = this.querySelector('.st-chart-card');
        this.symbolEl = this.querySelector('.st-chart-symbol');
        this.companyEl = this.querySelector('.st-chart-company');
        this.legendEl = this.querySelector('.st-chart-legend');
        this.canvasContainer = this.querySelector('.st-chart-canvas-container');

        // Button clicks
        const btns = this.querySelectorAll('.st-tf-btn');
        btns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const tf = btn.getAttribute('data-tf');
                this.selectTimeframe(tf);
            });
        });
    }

    selectTimeframe(tf) {
        if (this._currentTimeframe === tf) return;
        this._currentTimeframe = tf;
        this.updateTimeframeButtons();
        this.dispatchEvent(new CustomEvent('timeframe-changed', {
            detail: { timeframe: tf },
            bubbles: true,
            composed: true
        }));
    }

    updateTimeframeButtons() {
        const btns = this.querySelectorAll('.st-tf-btn');
        btns.forEach(btn => {
            if (btn.getAttribute('data-tf') === this._currentTimeframe) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });
    }

    initChart() {
        if (!this.canvasContainer) return;
        if (this.chart) return;

        const w = this.canvasContainer.clientWidth || 600;
        const h = this._height || 360;

        this.chart = createChart(this.canvasContainer, {
            width: w,
            height: h,
            layout: {
                background: { color: 'transparent' },
                textColor: '#A9A0A0',
                fontSize: 11,
                fontFamily: 'Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
            },
            grid: {
                vertLines: { color: 'rgba(57, 51, 64, 0.45)', style: 1 },
                horzLines: { color: 'rgba(57, 51, 64, 0.45)', style: 1 }
            },
            crosshair: {
                mode: CrosshairMode.Normal,
                vertLine: {
                    color: '#D18B5A99',
                    width: 1,
                    style: 2,
                    labelBackgroundColor: '#25212A'
                },
                horzLine: {
                    color: '#D18B5A99',
                    width: 1,
                    style: 2,
                    labelBackgroundColor: '#25212A'
                }
            },
            rightPriceScale: {
                borderColor: '#393340',
                visible: true,
                scaleMargins: {
                    top: 0.1,
                    bottom: 0.22
                }
            },
            timeScale: {
                borderColor: '#393340',
                timeVisible: true,
                secondsVisible: false
            },
            handleScroll: {
                mouseWheel: true,
                pressedMouseMove: true,
                horzTouchDrag: true,
                vertTouchDrag: false
            },
            handleScale: {
                axisPressedMouseMove: true,
                mouseWheel: true,
                pinch: true
            }
        });

        // Candlestick Series (Jade for bullish, Coral for bearish)
        this.candlestickSeries = this.chart.addCandlestickSeries({
            upColor: '#4FAE91',
            downColor: '#E47778',
            borderUpColor: '#4FAE91',
            borderDownColor: '#E47778',
            wickUpColor: '#4FAE91',
            wickDownColor: '#E47778'
        });

        // Volume Histogram (Sub-scale pinned to lower portion)
        this.volumeSeries = this.chart.addHistogramSeries({
            priceFormat: {
                type: 'volume'
            },
            priceScaleId: '',
            scaleMargins: {
                top: 0.8,
                bottom: 0.0
            }
        });

        // Interactive Crosshair Tooltip
        this.chart.subscribeCrosshairMove(param => {
            if (!param || !param.time || !param.seriesData || !param.seriesData.has(this.candlestickSeries)) {
                this.updateLegend(this.latestCandle);
            } else {
                const data = param.seriesData.get(this.candlestickSeries);
                const volData = param.seriesData.get(this.volumeSeries);
                this.updateLegend({
                    open: data.open,
                    high: data.high,
                    low: data.low,
                    close: data.close,
                    volume: volData ? volData.value : (this.latestCandle ? this.latestCandle.volume : 0),
                    time: param.time
                });
            }
        });

        // Responsive Resize Observer
        this.resizeObserver = new ResizeObserver(entries => {
            if (!entries || !entries.length) return;
            const entry = entries[0];
            const newWidth = Math.floor(entry.contentRect.width);
            if (newWidth > 50 && this.chart) {
                this.chart.applyOptions({ width: newWidth });
            }
        });
        this.resizeObserver.observe(this.canvasContainer);

        if (this.candleData && this.candleData.length > 0) {
            this.applyData(this.candleData);
        }
    }

    setData(candles) {
        let parsed = candles;
        if (typeof candles === 'string') {
            try {
                parsed = JSON.parse(candles);
            } catch (e) {
                console.error('[TradingChart] Error parsing JSON candle data:', e);
                return;
            }
        }
        this.candleData = Array.isArray(parsed) ? parsed : [];
        if (this.chart && this.candlestickSeries && this.volumeSeries) {
            this.applyData(this.candleData);
        }
    }

    applyData(data) {
        if (!data || data.length === 0) {
            if (this.candlestickSeries) this.candlestickSeries.setData([]);
            if (this.volumeSeries) this.volumeSeries.setData([]);
            this.updateLegend(null);
            return;
        }

        const ohlcItems = [];
        const volumeItems = [];

        for (let i = 0; i < data.length; i++) {
            const item = data[i];
            const isBullish = item.close >= item.open;
            ohlcItems.push({
                time: item.time,
                open: item.open,
                high: item.high,
                low: item.low,
                close: item.close
            });
            volumeItems.push({
                time: item.time,
                value: item.volume || 0,
                color: isBullish ? 'rgba(79, 174, 145, 0.4)' : 'rgba(228, 119, 120, 0.4)'
            });
        }

        this.candlestickSeries.setData(ohlcItems);
        this.volumeSeries.setData(volumeItems);

        this.latestCandle = data[data.length - 1];
        this.updateLegend(this.latestCandle);

        this.chart.timeScale().fitContent();
    }

    updateCandle(candle) {
        let parsed = candle;
        if (typeof candle === 'string') {
            try {
                parsed = JSON.parse(candle);
            } catch (e) {
                return;
            }
        }
        if (!parsed || !parsed.time || !this.candlestickSeries || !this.volumeSeries) return;

        const isBullish = parsed.close >= parsed.open;
        this.candlestickSeries.update({
            time: parsed.time,
            open: parsed.open,
            high: parsed.high,
            low: parsed.low,
            close: parsed.close
        });

        this.volumeSeries.update({
            time: parsed.time,
            value: parsed.volume || 0,
            color: isBullish ? 'rgba(79, 174, 145, 0.4)' : 'rgba(228, 119, 120, 0.4)'
        });

        this.latestCandle = parsed;
        this.updateLegend(parsed);
    }

    updateLegend(c) {
        if (!this.legendEl) return;

        if (!c) {
            this.legendEl.innerHTML = '<span class="st-chart-legend-muted">No historical candle data</span>';
            return;
        }

        let timeLabel = '';
        if (c.time) {
            const rawSec = typeof c.time === 'number' ? c.time : (c.time.timestamp || 0);
            if (rawSec > 0) {
                const d = new Date(rawSec * 1000);
                timeLabel = d.toLocaleString('en-IN', {
                    timeZone: 'Asia/Kolkata',
                    month: 'short',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false
                }) + ' IST';
            }
        }

        const open = Number(c.open).toFixed(2);
        const high = Number(c.high).toFixed(2);
        const low = Number(c.low).toFixed(2);
        const close = Number(c.close).toFixed(2);
        const change = (c.close - c.open);
        const changePct = c.open > 0 ? ((change / c.open) * 100).toFixed(2) : '0.00';
        const isUp = change >= 0;
        const colorClass = isUp ? 'st-positive' : 'st-negative';
        const sign = isUp ? '+' : '';

        const volFormatted = c.volume >= 1000000 
            ? (c.volume / 1000000).toFixed(2) + 'M'
            : (c.volume >= 1000 ? (c.volume / 1000).toFixed(1) + 'K' : String(c.volume));

        this.legendEl.innerHTML = `
            ${timeLabel ? `<span class="st-ohlc-item st-ohlc-time"><span class="tabular-num">${timeLabel}</span></span>` : ''}
            <span class="st-ohlc-item"><span class="st-ohlc-label">O</span> <span class="tabular-num">${open}</span></span>
            <span class="st-ohlc-item"><span class="st-ohlc-label">H</span> <span class="tabular-num">${high}</span></span>
            <span class="st-ohlc-item"><span class="st-ohlc-label">L</span> <span class="tabular-num">${low}</span></span>
            <span class="st-ohlc-item"><span class="st-ohlc-label">C</span> <span class="tabular-num">${close}</span></span>
            <span class="st-ohlc-item ${colorClass}"><span class="tabular-num">${sign}${changePct}%</span></span>
            <span class="st-ohlc-item"><span class="st-ohlc-label">Vol</span> <span class="tabular-num">${volFormatted}</span></span>
        `;
    }
}

if (!customElements.get('trading-chart')) {
    customElements.define('trading-chart', TradingChart);
}
