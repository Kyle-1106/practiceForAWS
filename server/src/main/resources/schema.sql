CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) NOT NULL UNIQUE,
    publisher VARCHAR(255),
    publication_year VARCHAR(4),
    status VARCHAR(20) DEFAULT '貸出可能',
    cover_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO books (title, author, isbn, publisher, publication_year, status, cover_image) 
VALUES 
    ('Spring Boot入門', '山田太郎', '9784123456781', '技術書出版', '2023', '貸出可能', 
     'https://picsum.photos/seed/spring-boot/300/400'),
    ('Vue.js実践ガイド', '鈴木一郎', '9784123456782', 'Webプログラミング社', '2023', '貸出中', 
     'https://picsum.photos/seed/vuejs/300/400'),
    ('データベース設計入門', '佐藤花子', '9784123456783', 'DB技術社', '2022', '貸出可能', 
     'https://picsum.photos/seed/database/300/400'),
    ('AWS実践入門', '田中次郎', '9784123456784', 'クラウド技術社', '2023', '貸出可能', 
     'https://picsum.photos/seed/aws/300/400'),
    ('Docker完全ガイド', '高橋三郎', '9784123456785', 'インフラ技術社', '2022', '貸出中', 
     'https://picsum.photos/seed/docker/300/400')
ON CONFLICT (isbn) DO NOTHING;

-- 予約テーブルの作成
CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id),
    status VARCHAR(20) NOT NULL DEFAULT '予約中',
    reserved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    borrowed_at TIMESTAMP,
    returned_at TIMESTAMP,
    UNIQUE(book_id, status)
);

-- ユーザーのサンプルデータ
INSERT INTO users (email, password, name) 
VALUES 
    ('test@example.com', 'Test@123', 'テストユーザー'),
    ('user1@example.com', 'User1@123', '山田太郎'),
    ('user2@example.com', 'User2@123', '鈴木花子')
ON CONFLICT (email) DO NOTHING;

-- 書籍のサンプルデータを更新（既存のINSERT文を修正）
INSERT INTO books (title, author, isbn, publisher, publication_year, status, cover_image) 
VALUES 
    ('Javaプログラミング入門', '田中一郎', '9784123456786', 'ITプログラミング社', '2023', '貸出可能', 
     'https://picsum.photos/seed/java/300/400'),
    ('Python実践ガイド', '佐藤二郎', '9784123456787', 'プログラミング出版', '2023', '貸出可能', 
     'https://picsum.photos/seed/python/300/400'),
    ('React.js入門', '山本三郎', '9784123456788', 'フロントエンド社', '2023', '貸出中', 
     'https://picsum.photos/seed/react/300/400'),
    ('SQL基礎から応用', '中村四郎', '9784123456789', 'データベース出版', '2023', '貸出可能', 
     'https://picsum.photos/seed/sql/300/400'),
    ('Docker & Kubernetes入門', '小林五郎', '9784123456790', 'インフラ出版', '2023', '予約済み', 
     'https://picsum.photos/seed/kubernetes/300/400')
ON CONFLICT (isbn) DO NOTHING;

-- 予約のサンプルデータ
INSERT INTO reservations (user_id, book_id, status, reserved_at) 
SELECT 
    u.id as user_id,
    b.id as book_id,
    '予約中',
    CURRENT_TIMESTAMP
FROM 
    users u 
    CROSS JOIN books b 
WHERE 
    u.email = 'test@example.com' 
    AND b.title = 'Docker & Kubernetes入門'
ON CONFLICT (book_id, status) DO NOTHING;

-- 貸出中の本のサンプルデータを追加
INSERT INTO reservations (user_id, book_id, status, reserved_at, borrowed_at) 
SELECT 
    u.id as user_id,
    b.id as book_id,
    '貸出中',
    CURRENT_TIMESTAMP - INTERVAL '7 days',  -- 1週間前に予約
    CURRENT_TIMESTAMP - INTERVAL '5 days'   -- 5日前に貸出
FROM 
    users u 
    CROSS JOIN books b 
WHERE 
    u.email = 'test@example.com' 
    AND b.title IN ('Spring Boot入門', 'Vue.js実践ガイド')
ON CONFLICT (book_id, status) DO NOTHING;

-- 対応する本の状態を更新
UPDATE books 
SET status = '貸出中' 
WHERE title IN ('Spring Boot入門', 'Vue.js実践ガイド')
  AND NOT EXISTS (
    SELECT 1 
    FROM reservations r 
    WHERE r.book_id = books.id 
    AND r.status = '貸出中'
  );

-- 返却期限を2週間後に設定するカラムを追加
ALTER TABLE reservations 
ADD COLUMN IF NOT EXISTS due_date TIMESTAMP;

-- 貸出中の本に返却期限を設定
UPDATE reservations 
SET due_date = borrowed_at + INTERVAL '14 days'
WHERE status = '貸出中' 
  AND due_date IS NULL; 