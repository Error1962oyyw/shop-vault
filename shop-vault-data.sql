INSERT INTO `shop-vault`.oms_cart_item (id, user_id, product_id, quantity, create_time) VALUES (1, 2, 1, 4, '2026-02-28 23:28:11');
INSERT INTO `shop-vault`.oms_cart_item (id, user_id, product_id, quantity, create_time) VALUES (2, 2, 2, 1, '2026-02-28 23:33:00');

INSERT INTO `shop-vault`.oms_order (id, order_no, user_id, total_amount, pay_amount, status, receiver_snapshot, tracking_company, tracking_no, payment_time, delivery_time, receive_time, create_time) VALUES (1, '2027769951049318400', 2, 49.70, 49.70, 4, null, null, null, null, null, null, '2026-02-28 23:36:45');
INSERT INTO `shop-vault`.oms_order (id, order_no, user_id, total_amount, pay_amount, status, receiver_snapshot, tracking_company, tracking_no, payment_time, delivery_time, receive_time, create_time) VALUES (2, '2028074072910446592', 2, 69.70, 55.76, 3, null, null, null, null, null, null, '2026-03-01 19:45:13');
INSERT INTO `shop-vault`.oms_order (id, order_no, user_id, total_amount, pay_amount, status, receiver_snapshot, tracking_company, tracking_no, payment_time, delivery_time, receive_time, create_time) VALUES (3, '2028166562447421440', 2, 29.90, 29.90, 0, null, null, null, null, null, null, '2026-03-02 01:52:44');
INSERT INTO `shop-vault`.oms_order (id, order_no, user_id, total_amount, pay_amount, status, receiver_snapshot, tracking_company, tracking_no, payment_time, delivery_time, receive_time, create_time) VALUES (4, '2028166951171321856', 2, 29.90, 29.90, 0, null, null, null, null, null, null, '2026-03-02 01:54:17');
INSERT INTO `shop-vault`.oms_order (id, order_no, user_id, total_amount, pay_amount, status, receiver_snapshot, tracking_company, tracking_no, payment_time, delivery_time, receive_time, create_time) VALUES (5, '2028166958087729152', 2, 29.90, 29.90, 4, null, null, null, null, null, null, '2026-03-02 01:54:18');

INSERT INTO `shop-vault`.oms_order_item (id, order_id, order_no, product_id, product_name, product_img, product_price, quantity) VALUES (1, 1, '2027769951049318400', 1, '杯子测试', 'https://img.alicdn.com/imgextra/i2/806364910/O1CN01HmZQin1m8pXgiYLSs_!!806364910.jpg', 29.90, 1);
INSERT INTO `shop-vault`.oms_order_item (id, order_id, order_no, product_id, product_name, product_img, product_price, quantity) VALUES (2, 1, '2027769951049318400', 2, '书测试', 'https://img.alicdn.com/i4/340039805/O1CN01OlRuh12MIk9UkcQyt_!!340039805.jpg', 9.90, 2);
INSERT INTO `shop-vault`.oms_order_item (id, order_id, order_no, product_id, product_name, product_img, product_price, quantity) VALUES (3, 2, '2028074072910446592', 1, '杯子测试', 'https://img.alicdn.com/imgextra/i2/806364910/O1CN01HmZQin1m8pXgiYLSs_!!806364910.jpg', 29.90, 2);
INSERT INTO `shop-vault`.oms_order_item (id, order_id, order_no, product_id, product_name, product_img, product_price, quantity) VALUES (4, 2, '2028074072910446592', 2, '书测试', 'https://img.alicdn.com/i4/340039805/O1CN01OlRuh12MIk9UkcQyt_!!340039805.jpg', 9.90, 1);
INSERT INTO `shop-vault`.oms_order_item (id, order_id, order_no, product_id, product_name, product_img, product_price, quantity) VALUES (5, 3, '2028166562447421440', 1, '杯子测试', 'https://img.alicdn.com/imgextra/i2/806364910/O1CN01HmZQin1m8pXgiYLSs_!!806364910.jpg', 29.90, 1);
INSERT INTO `shop-vault`.oms_order_item (id, order_id, order_no, product_id, product_name, product_img, product_price, quantity) VALUES (6, 4, '2028166951171321856', 1, '杯子测试', 'https://img.alicdn.com/imgextra/i2/806364910/O1CN01HmZQin1m8pXgiYLSs_!!806364910.jpg', 29.90, 1);
INSERT INTO `shop-vault`.oms_order_item (id, order_id, order_no, product_id, product_name, product_img, product_price, quantity) VALUES (7, 5, '2028166958087729152', 1, '杯子测试', 'https://img.alicdn.com/imgextra/i2/806364910/O1CN01HmZQin1m8pXgiYLSs_!!806364910.jpg', 29.90, 1);

INSERT INTO `shop-vault`.pms_category (id, name, parent_id, level, icon, sort) VALUES (1, '杯子', 0, 1, null, 0);
INSERT INTO `shop-vault`.pms_category (id, name, parent_id, level, icon, sort) VALUES (2, '书籍', 0, 1, null, 0);

INSERT INTO `shop-vault`.pms_product (id, category_id, name, sub_title, main_image, price, stock, stock_warning, status, sales, detail_html, create_time) VALUES (1, 1, '杯子测试', '欧式陶瓷马克杯', 'https://img.alicdn.com/imgextra/i2/806364910/O1CN01HmZQin1m8pXgiYLSs_!!806364910.jpg', 29.90, 94, 10, 1, 0, null, '2026-02-28 15:11:11');
INSERT INTO `shop-vault`.pms_product (id, category_id, name, sub_title, main_image, price, stock, stock_warning, status, sales, detail_html, create_time) VALUES (2, 2, '书测试', 'C程序设计-谭浩强', 'https://img.alicdn.com/i4/340039805/O1CN01OlRuh12MIk9UkcQyt_!!340039805.jpg', 9.90, 47, 10, 1, 0, null, '2026-02-28 15:47:46');

INSERT INTO `shop-vault`.sys_user (id, username, password, nickname, avatar, phone, email, balance, points, status, role, create_time) VALUES (1, 'testadmin', '$2a$10$QjodEKdXH4AcUPncH5W./ulsOyZ1a5z1spgUrm4PyeL37R/d91vr2', null, null, null, null, 0.00, 0, 1, 'ADMIN', '2026-02-24 01:24:29');
INSERT INTO `shop-vault`.sys_user (id, username, password, nickname, avatar, phone, email, balance, points, status, role, create_time) VALUES (2, 'testuser', '$2a$10$yPH1XTmKpwoFUq9bQIA4oOHhbMnMn0wljKqchf/6OPpzTTGLVs8S.', null, null, null, null, 0.00, 15, 1, 'USER', '2026-02-28 23:25:02');

INSERT INTO `shop-vault`.sys_yolo_mapping (id, yolo_label, category_id, confidence_threshold, is_active) VALUES (1, 'cup', 1, null, 1);
INSERT INTO `shop-vault`.sys_yolo_mapping (id, yolo_label, category_id, confidence_threshold, is_active) VALUES (2, 'book', 2, null, 1);
