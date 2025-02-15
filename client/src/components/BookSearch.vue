<template>
  <div class="search-page">
    <div class="search-container">
      <!-- ヘッダー -->
      <div class="header">
        <div class="logo">
          <img src="@/assets/logo.png" alt="Book Manager Logo">
          <h2 class="service-name">Book Manager</h2>
        </div>
        <div class="nav-links">
          <router-link to="/dashboard" class="nav-link">マイライブラリ</router-link>
          <a href="#" @click.prevent="handleLogout" class="nav-link">ログアウト</a>
        </div>
      </div>

      <!-- 検索フォーム -->
      <div class="search-form-container">
        <h1>書籍検索</h1>
        <form class="search-form" @submit.prevent="searchBooks">
          <div class="form-group">
            <label for="title">タイトル</label>
            <div class="input-wrapper">
              <input 
                type="text" 
                id="title" 
                v-model="searchParams.title" 
                placeholder="タイトルで検索"
              >
            </div>
          </div>

          <div class="form-group">
            <label for="author">著者</label>
            <div class="input-wrapper">
              <input 
                type="text" 
                id="author" 
                v-model="searchParams.author" 
                placeholder="著者名で検索"
              >
            </div>
          </div>

          <div class="form-group">
            <label for="isbn">ISBN</label>
            <div class="input-wrapper">
              <input 
                type="text" 
                id="isbn" 
                v-model="searchParams.isbn" 
                placeholder="ISBNで検索"
              >
            </div>
          </div>

          <div class="form-group">
            <button type="submit" class="search-button">検索</button>
          </div>
        </form>
      </div>

      <!-- 検索結果 -->
      <div class="search-results" v-if="books.length > 0">
        <div v-for="book in books" :key="book.id" class="book-card">
          <img 
            :src="book.coverImage" 
            :alt="book.title"
            class="book-cover"
            @error="handleImageError(book)"
          >
          <div class="book-info">
            <h3>{{ book.title }}</h3>
            <p class="author">{{ book.author }}</p>
            <p class="isbn">ISBN: {{ book.isbn }}</p>
            <p class="status" :class="book.status">{{ book.status }}</p>
            <button 
              @click="reserveBook(book.id)" 
              :disabled="book.status !== '貸出可能'"
              class="reserve-button"
            >
              {{ book.status === '貸出可能' ? '予約する' : book.status }}
            </button>
          </div>
        </div>
      </div>

      <p v-else-if="searched" class="no-results">
        検索結果が見つかりませんでした
      </p>
    </div>

    <!-- フッターリンク -->
    <div class="footer-links">
      <a href="#">プライバシーポリシー</a>
      <a href="#">利用規約</a>
      <a href="#">お問い合わせ</a>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth'
import { ApiEndpoints } from '@/consts/api'

export default {
  name: 'BookSearch',
  components: {
  },
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  data() {
    return {
      searchParams: {
        title: '',
        author: '',
        isbn: ''
      },
      books: [],
      searched: false
    }
  },
  methods: {
    async searchBooks() {
      try {
        const queryParams = new URLSearchParams()
        if (this.searchParams.title) queryParams.append('title', this.searchParams.title)
        if (this.searchParams.author) queryParams.append('author', this.searchParams.author)
        if (this.searchParams.isbn) queryParams.append('isbn', this.searchParams.isbn)

        const response = await fetch(`${ApiEndpoints.BOOKS.SEARCH}?${queryParams}`, {
          headers: {
            'Authorization': `Bearer ${this.authStore.token}`
          }
        })

        if (!response.ok) {
          throw new Error('検索に失敗しました')
        }

        this.books = await response.json()
        this.searched = true
      } catch (error) {
        console.error('Search error:', error)
        alert(error.message)
      }
    },
    async reserveBook(bookId) {
      try {
        if (!this.authStore.user?.id) {
          throw new Error('ユーザー情報が取得できません。ログインしてください');
        }

        const response = await fetch(ApiEndpoints.RESERVATIONS.CREATE, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.authStore.token}`
          },
          body: JSON.stringify({
            bookId: bookId,
            userId: this.authStore.user.id
          })
        });

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || '予約に失敗しました');
        }

        // 予約成功後、書籍の状態を更新
        const updatedBook = await response.json();
        const index = this.books.findIndex(book => book.id === bookId);
        if (index !== -1) {
          this.books[index].status = updatedBook.status;
        }

        alert('予約が完了しました');
      } catch (error) {
        console.error('Reservation error:', error);
        alert(error.message);
      }
    },
    async handleLogout() {
      try {
        await fetch(`${ApiEndpoints.AUTH.LOGOUT}`, {
          method: 'POST',
          credentials: 'include'
        });
        this.$router.push('/login');
      } catch (error) {
        console.error('ログアウトエラー:', error);
      }
    },
    async getBookImage(book) {
      try {
        // Google Books APIを使用して画像を取得
        const response = await fetch(
          `https://www.googleapis.com/books/v1/volumes?q=isbn:${book.isbn}`
        );
        const data = await response.json();
        
        if (data.items && data.items[0]?.volumeInfo?.imageLinks?.thumbnail) {
          // HTTPSのURLを取得（Google BooksのURLはHTTPSである必要がある）
          const imageUrl = data.items[0].volumeInfo.imageLinks.thumbnail.replace('http:', 'https:');
          return imageUrl;
        }
        
        // 画像が見つからない場合はダミー画像を使用
        return this.getDefaultImage(book);
      } catch (error) {
        console.error('Error fetching book image:', error);
        return this.getDefaultImage(book);
      }
    },
    getDefaultImage(book) {
      // タイトルとISBNを使用してユニークな画像を生成
      const seed = `${book.title}-${book.isbn}`;
      return `https://picsum.photos/seed/${encodeURIComponent(seed)}/300/400`;
    },
    handleImageError(book) {
      // 画像読み込みエラー時にダミー画像を表示
      const img = event.target;
      const seed = `${book.title}-${book.isbn}`;
      img.src = `https://picsum.photos/seed/${encodeURIComponent(seed)}/300/400`;
    }
  }
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background-color: #f7f7f7;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem 1rem;
}

.search-container {
  background: white;
  max-width: 1200px;
  width: 100%;
  padding: 2.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.logo {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo img {
  height: 40px;
  width: auto;
}

.service-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
}

.nav-links {
  display: flex;
  gap: 1.5rem;
}

.nav-link {
  color: #666;
  text-decoration: none;
  font-size: 0.875rem;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #ff6b00;
}

.search-form-container {
  margin-bottom: 2rem;
}

.search-form-container h1 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
}

.search-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #333;
  margin-bottom: 0.5rem;
}

.input-wrapper {
  position: relative;
}

input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #ff6b00;
  box-shadow: 0 0 0 2px rgba(255, 107, 0, 0.1);
}

.search-button {
  width: 100%;
  padding: 0.875rem;
  background-color: #ff6b00;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.search-button:hover {
  background-color: #e65c00;
}

.search-results {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
}

.book-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  background-color: #f5f5f5;
}

.book-info {
  padding: 1rem;
}

.book-info h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}

.author {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.isbn {
  color: #999;
  font-size: 0.8rem;
  margin-bottom: 0.5rem;
}

.status {
  font-weight: 500;
  margin-bottom: 1rem;
}

.reserve-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #ff6b00;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.reserve-button:hover {
  background-color: #e65c00;
}

.reserve-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.no-results {
  text-align: center;
  color: #666;
  padding: 2rem;
}

.footer-links {
  display: flex;
  gap: 1.5rem;
  margin-top: 2rem;
}

.footer-links a {
  color: #666;
  font-size: 0.75rem;
  text-decoration: none;
}

.footer-links a:hover {
  color: #333;
}

@media (max-width: 640px) {
  .search-container {
    padding: 1.5rem;
  }

  .header {
    flex-direction: column;
    gap: 1rem;
  }

  .nav-links {
    width: 100%;
    justify-content: center;
  }

  .footer-links {
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }
}
</style> 