<template>
  <div class="dashboard">
    <!-- ハンバーガーメニュー -->
    <div class="hamburger-menu">
      <button 
        class="menu-toggle" 
        @click="toggleMenu"
        :class="{ 'is-active': isMenuOpen }"
      >
        <span></span>
        <span></span>
        <span></span>
      </button>

      <!-- メニューの内容 -->
      <nav :class="{ 'is-open': isMenuOpen }">
        <ul>
          <li>
            <router-link to="/search" class="menu-link" @click="closeMenu">
              <i class="fas fa-search"></i>書籍検索
            </router-link>
          </li>
          <li>
            <a href="#" @click.prevent="handleLogout" class="menu-link">
              <i class="fas fa-sign-out-alt"></i>ログアウト
            </a>
          </li>
        </ul>
      </nav>

      <!-- オーバーレイ -->
      <div 
        class="overlay" 
        :class="{ 'is-visible': isMenuOpen }"
        @click="closeMenu"
      ></div>
    </div>

    <div class="dashboard-container">
      <!-- ヘッダー -->
      <div class="dashboard-header">
        <h1>マイライブラリ</h1>
      </div>

      <!-- 借りている本 -->
      <section class="book-section">
        <h2>借りている本</h2>
        <div v-if="borrowedBooks.length > 0" class="books-grid">
          <div v-for="book in borrowedBooks" :key="book.id" class="book-card">
            <div class="book-image">
              <img 
                :src="book.coverImage" 
                :alt="book.title"
                @error="handleImageError(book)"
              >
              <div class="book-status">貸出中</div>
            </div>
            <div class="book-info">
              <h3>{{ book.title }}</h3>
              <p class="author">{{ book.author }}</p>
              <p class="due-date">返却期限: {{ formatDate(book.dueDate) }}</p>
            </div>
          </div>
        </div>
        <p v-else class="no-books">現在借りている本はありません</p>
      </section>

      <!-- 予約中の本 -->
      <section class="book-section">
        <h2>予約中の本</h2>
        <div v-if="reservedBooks.length > 0" class="books-grid">
          <div v-for="book in reservedBooks" :key="book.id" class="book-card">
            <div class="book-image">
              <img 
                :src="book.coverImage" 
                :alt="book.title"
                @error="handleImageError(book)"
              >
              <div class="book-status reserved">予約中</div>
            </div>
            <div class="book-info">
              <h3>{{ book.title }}</h3>
              <p class="author">{{ book.author }}</p>
              <p class="isbn">ISBN: {{ book.isbn }}</p>
              <p class="publisher">出版社: {{ book.publisher }}</p>

            </div>
          </div>
        </div>
        <p v-else class="no-books">予約中の本はありません</p>
      </section>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth'
import { ApiEndpoints } from '@/consts/api'

export default {
  name: 'Dashboard',
  components: {
    // HamburgerMenuを削除
  },
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  data() {
    return {
      borrowedBooks: [],
      reservedBooks: [],
      isMenuOpen: false
    }
  },
  async mounted() {
    // ユーザー情報が取得できるまで待機
    if (!this.authStore.user) {
      await new Promise(resolve => {
        const checkUser = setInterval(() => {
          if (this.authStore.user) {
            clearInterval(checkUser);
            resolve();
          }
        }, 100);
      });
    }
    
    // 両方のデータを並行して取得
    await Promise.all([
      this.fetchBorrowedBooks(),
      this.fetchReservedBooks()
    ]);
  },
  methods: {
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
      document.body.style.overflow = this.isMenuOpen ? 'hidden' : '';
    },
    closeMenu() {
      this.isMenuOpen = false;
      document.body.style.overflow = '';
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString('ja-JP', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },
    async handleLogout() {
      try {
        const authStore = useAuthStore()
        authStore.logout()
        await this.$router.push('/login')
      } catch (error) {
        console.error('ログアウトエラー:', error)
      }
    },
    async fetchBorrowedBooks() {
      try {
        if (!this.authStore.user?.id) {
          console.log('ユーザー情報が取得できていません');
          return;
        }
        const response = await fetch(
          ApiEndpoints.BOOKS.BORROWED(this.authStore.user.id),
          {
            headers: {
              'Authorization': `Bearer ${this.authStore.token}`
            }
          }
        );
        
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || '貸出中の本の取得に失敗しました');
        }
        
        this.borrowedBooks = await response.json();
      } catch (error) {
        console.error('Error fetching borrowed books:', error);
      }
    },
    async fetchReservedBooks() {
      try {
        if (!this.authStore.user?.id) {
          console.log('ユーザー情報が取得できていません');
          return;
        }
        const response = await fetch(
          ApiEndpoints.BOOKS.RESERVED(this.authStore.user.id),
          {
            headers: {
              'Authorization': `Bearer ${this.authStore.token}`
            }
          }
        );
        
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || '予約中の本の取得に失敗しました');
        }
        
        this.reservedBooks = await response.json();
      } catch (error) {
        console.error('Error fetching reserved books:', error);
      }
    },
    handleImageError(book) {
      const img = event.target;
      const seed = `${book.title}-${book.isbn}`;
      img.src = `https://picsum.photos/seed/${encodeURIComponent(seed)}/300/400`;
    }
  }
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background-color: #f7f7f7;
  padding: 2rem;
  padding-left: calc(250px + 2rem); /* ハンバーガーメニューの幅 + 余白 */
}

.dashboard-container {
  max-width: 1200px;
  margin: 0 auto; /* 中央揃え */
  width: 100%;
  padding: 0 2rem;
}

/* ハンバーガーメニュー */
.hamburger-menu {
  position: fixed;
  top: 1rem;
  left: 1rem;
  z-index: 1000;
}

.menu-toggle {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 30px;
  height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  z-index: 1001;
}

.menu-toggle span {
  display: block;
  width: 100%;
  height: 2px;
  background-color: #333;
  transition: all 0.3s ease;
}

.menu-toggle.is-active span:nth-child(1) {
  transform: translateY(11px) rotate(45deg);
}

.menu-toggle.is-active span:nth-child(2) {
  opacity: 0;
}

.menu-toggle.is-active span:nth-child(3) {
  transform: translateY(-11px) rotate(-45deg);
}

nav {
  position: fixed;
  top: 0;
  left: -250px;
  width: 250px;
  height: 100vh;
  background-color: white;
  padding: 4rem 1rem;
  transition: transform 0.3s ease;
  z-index: 999;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

nav.is-open {
  transform: translateX(250px);
}

nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

nav li {
  margin-bottom: 1rem;
}

.menu-link {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  color: #333;
  text-decoration: none;
  font-size: 1rem;
  transition: all 0.3s ease;
  border-radius: 4px;
}

.menu-link:hover {
  background-color: #f5f5f5;
  color: #ff6b00;
}

.menu-link i {
  margin-right: 0.75rem;
  width: 20px;
  text-align: center;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 998;
}

.overlay.is-visible {
  opacity: 1;
  visibility: visible;
}

/* ダッシュボードコンテンツ */
.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
}

.book-section {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.book-section h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1.5rem;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

.book-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-image {
  position: relative;
  height: 200px;
}

.book-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-status {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  padding: 0.25rem 0.75rem;
  background-color: #ff6b00;
  color: white;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.book-status.reserved {
  background-color: #fbbf24;
}

.book-info {
  padding: 1rem;
}

.book-info h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
  line-height: 1.4;
}

.author {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.isbn {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.publisher {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.due-date, .reservation-date {
  color: #ff6b00;
  font-size: 0.875rem;
  font-weight: 500;
}

.no-books {
  text-align: center;
  color: #666;
  padding: 2rem;
  background: #f9f9f9;
  border-radius: 4px;
}

@media (max-width: 640px) {
  .hamburger-menu {
    top: 0.5rem;
    left: 0.5rem;
  }

  nav {
    width: 220px;
    left: -220px;
  }

  nav.is-open {
    transform: translateX(220px);
  }
}
</style>
