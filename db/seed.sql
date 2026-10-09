USE papertrade;

-- Passwords: admin -> Admin@123 ; rahul, priya -> Demo@123
-- Hash format: Base64(salt):Base64(PBKDF2WithHmacSHA256, 65536 iterations, 256-bit)
INSERT INTO users (id, username, email, full_name, password_hash, role, status, cash_balance) VALUES
(1, 'admin', 'admin@papertrade.local', 'Platform Admin', '0+3DUVGW/WjfLstenwOF3w==:opvQZCOrEjgqCLsOOnh8rXweOBhILK0+zdi/c1ELKOk=', 'ADMIN', 'ACTIVE', 0.00),
(2, 'rahul', 'rahul@papertrade.local', 'Rahul Sharma', 'GkhX+Tj0sacIqQQ0GZC6XA==:Uqu0H3XXIMG9lS3wtbuIwdmk6fbIXMCdBHtsSaZJi4E=', 'USER', 'ACTIVE', 100000.00),
(3, 'priya', 'priya@papertrade.local', 'Priya Verma', 'wBU07JGZc/buhmqgpMrkaw==:t2pmqPYP34CL8gfaMhpqRZt89Vn7RuVsK7nONk4yOWk=', 'USER', 'ACTIVE', 21600.00);

-- Mock prices. NOT real market quotes.
INSERT INTO stocks (id, symbol, company_name, sector, description, price, prev_price) VALUES
(1, 'RELIANCE', 'Reliance Industries Ltd', 'Energy', 'Conglomerate: energy, retail, telecom.', 2850.00, 2850.00),
(2, 'TCS', 'Tata Consultancy Services', 'IT', 'IT services and consulting.', 3900.00, 3900.00),
(3, 'INFY', 'Infosys Ltd', 'IT', 'IT services and consulting.', 1500.00, 1500.00),
(4, 'HDFCBANK', 'HDFC Bank Ltd', 'Banking', 'Private sector bank.', 1650.00, 1650.00),
(5, 'ICICIBANK', 'ICICI Bank Ltd', 'Banking', 'Private sector bank.', 1200.00, 1200.00),
(6, 'SBIN', 'State Bank of India', 'Banking', 'Public sector bank.', 800.00, 800.00),
(7, 'ITC', 'ITC Ltd', 'FMCG', 'FMCG, hotels, agri-business.', 430.00, 430.00),
(8, 'LT', 'Larsen & Toubro Ltd', 'Infrastructure', 'Engineering and construction.', 3600.00, 3600.00),
(9, 'BHARTIARTL', 'Bharti Airtel Ltd', 'Telecom', 'Telecom services.', 1400.00, 1400.00),
(10, 'HINDUNILVR', 'Hindustan Unilever Ltd', 'FMCG', 'Consumer goods.', 2400.00, 2400.00),
(11, 'WIPRO', 'Wipro Ltd', 'IT', 'IT services and consulting.', 480.00, 480.00),
(12, 'TATAMOTORS', 'Tata Motors Ltd', 'Automobile', 'Passenger and commercial vehicles.', 950.00, 950.00),
(13, 'MARUTI', 'Maruti Suzuki India Ltd', 'Automobile', 'Passenger cars.', 12500.00, 12500.00),
(14, 'ASIANPAINT', 'Asian Paints Ltd', 'Consumer', 'Decorative paints.', 2900.00, 2900.00),
(15, 'SUNPHARMA', 'Sun Pharmaceutical Industries', 'Pharma', 'Generic and specialty pharma.', 1700.00, 1700.00);

-- Priya: pre-built history. Math: 100000 - 38000 - 29000 - 20000 + 8600 = 21600 (matches users.cash_balance)
INSERT INTO trades (user_id, stock_id, trade_type, quantity, price, total_amount, realized_pnl, executed_at) VALUES
(3, 2, 'BUY', 10, 3800.00, 38000.00, 0.00, '2026-10-01 10:00:00'),
(3, 3, 'BUY', 20, 1450.00, 29000.00, 0.00, '2026-10-01 10:05:00'),
(3, 7, 'BUY', 50, 400.00, 20000.00, 0.00, '2026-10-02 11:00:00'),
(3, 7, 'SELL', 20, 430.00, 8600.00, 600.00, '2026-10-03 14:30:00');

INSERT INTO holdings (user_id, stock_id, quantity, avg_buy_price) VALUES
(3, 2, 10, 3800.0000),
(3, 3, 20, 1450.0000),
(3, 7, 30, 400.0000);
