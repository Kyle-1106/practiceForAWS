<template>
  <div class="login-page">
    <div class="login-container">
      <!-- ロゴ -->
      <div class="logo">
        <img 
          src="@/assets/logo.png" 
          alt="Book Manager Logo" 
        >
        <h2 class="service-name">Book Manager</h2>
      </div>

      <!-- タイトル -->
      <div class="title">
        <h1>ログイン</h1>
      </div>

      <!-- ログインフォーム -->
      <form class="login-form" @submit.prevent="handleLogin">
        <!-- 一般的なエラーメッセージ -->
        <div v-if="errors.general" class="error-message general-error" role="alert">
          {{ errors.general }}
        </div>

        <!-- メールアドレス入力 -->
        <div class="form-group">
          <label for="email">メールアドレス</label>
          <div class="input-wrapper">
            <input 
              type="email" 
              id="email" 
              v-model="email" 
              required 
              @blur="validateEmail"
              placeholder="example@email.com"
            >
            <p v-if="errors.email" class="error-message">
              {{ errors.email }}
            </p>
          </div>
        </div>

        <!-- パスワード入力 -->
        <div class="form-group">
          <label for="password">パスワード</label>
          <div class="input-wrapper">
            <div class="password-field">
              <input 
                :type="showPassword ? 'text' : 'password'"
                id="password" 
                v-model="password" 
                required 
                @blur="validatePassword"
                placeholder="パスワードを入力"
              >
              <button 
                type="button"
                @click="showPassword = !showPassword"
                class="toggle-password"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="eye-icon">
                  <path v-if="!showPassword" stroke-linecap="round" stroke-linejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" />
                  <path v-if="!showPassword" stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path v-if="showPassword" stroke-linecap="round" stroke-linejoin="round" d="M3.98 8.223A10.477 10.477 0 001.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.45 10.45 0 0112 4.5c4.756 0 8.773 3.162 10.065 7.498a10.523 10.523 0 01-4.293 5.774M6.228 6.228L3 3m3.228 3.228l3.65 3.65m7.894 7.894L21 21m-3.228-3.228l-3.65-3.65m0 0a3 3 0 10-4.243-4.243m4.242 4.242L9.88 9.88" />
                </svg>
              </button>
            </div>
            <p v-if="errors.password" class="error-message">
              {{ errors.password }}
            </p>
          </div>
        </div>

        

        <!-- ログインボタン -->
        <div class="form-group">
          <button 
            type="submit" 
            :disabled="!isFormValid || isLoading"
            class="login-button"
          >
            <span v-if="isLoading">ログイン中...</span>
            <span v-else>ログイン</span>
          </button>
        </div>
      </form>

      <!-- アカウント作成リンク -->
      <div class="register-link">
        <p>アカウントをお持ちでない方</p>
        <router-link to="/register" class="register-button">
          新規会員登録
        </router-link>
      </div>
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
  name: 'LoginPage',
  data() {
    return {
      email: '',
      password: '',
      showPassword: false,
      isLoading: false,
      errors: {
        email: '',
        password: '',
        general: ''
      }
    }
  },
  computed: {
    isFormValid() {
      return this.email && 
             this.password && 
             !this.errors.email && 
             !this.errors.password;
    }
  },
  methods: {
    validateEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.email) {
        this.errors.email = 'メールアドレスを入力してください';
      } else if (!emailRegex.test(this.email)) {
        this.errors.email = '正しいメールアドレスの形式で入力してください';
      } else {
        this.errors.email = '';
      }
    },
    validatePassword() {
      if (!this.password) {
        this.errors.password = 'パスワードを入力してください';
      } else if (this.password.length < 5) {
        this.errors.password = 'パスワードは5文字以上で入力してください';
      } else if (!/[A-Z]/.test(this.password)) {
        this.errors.password = '大文字を含める必要があります';
      } else if (!/[0-9]/.test(this.password)) {
        this.errors.password = '数字を含める必要があります';
      } else {
        this.errors.password = '';
      }
    },
    async handleLogin() {
      this.validateEmail();
      this.validatePassword();
      
      if (this.isFormValid) {
        this.isLoading = true;
        this.errors.general = '';
        
        try {
          const response = await fetch(ApiEndpoints.AUTH.LOGIN, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              email: this.email,
              password: this.password
            }),
            credentials: 'include'
          });

          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'ログインに失敗しました');
          }

          const data = await response.json();
          const authStore = useAuthStore();
          authStore.setToken(data.token);
          authStore.setUser(data.user);
          await this.$router.push('/dashboard');
        } catch (error) {
          console.error('Login error:', error);
          this.errors.general = error.message || 'ログインに失敗しました。もう一度お試しください。';
        } finally {
          this.isLoading = false;
        }
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background-color: #f7f7f7;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem 1rem;
}

.login-container {
  background: white;
  max-width: 440px;
  width: 100%;
  padding: 2.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.logo {
  text-align: center;
  margin-bottom: 2rem;
}

.logo img {
  height: 64px;
  width: auto;
  margin-bottom: 0.5rem;
}

.service-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
  margin-top: 0.5rem;
}

.title {
  text-align: center;
  margin-bottom: 2rem;
}

.title h1 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

.login-form {
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
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

.password-field {
  position: relative;
}

.toggle-password {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.eye-icon {
  width: 1.5rem;
  height: 1.5rem;
}

.error-message {
  color: #dc2626;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.forgot-password {
  text-align: center;
  margin: 1rem 0;
}

.forgot-password a {
  color: #666;
  font-size: 0.875rem;
  text-decoration: none;
}

.forgot-password a:hover {
  color: #ff6b00;
  text-decoration: underline;
}

.login-button {
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

.login-button:hover {
  background-color: #e65c00;
}

.login-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.register-link {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.register-link p {
  color: #666;
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
}

.register-link .register-button {
  color: #ff6b00;
  font-weight: 500;
  text-decoration: none;
}

.register-link .register-button:hover {
  text-decoration: underline;
}

.footer-links {
  margin-top: 2rem;
  display: flex;
  gap: 1.5rem;
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
  .login-container {
    padding: 1.5rem;
  }
  
  .footer-links {
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }
}
</style>
