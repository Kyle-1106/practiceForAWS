/**
 * APIエンドポイントを管理するクラス
 */
export class ApiEndpoints {
  // 環境変数からベースURLを取得
  
  static BASE_URL = `${process.env.VUE_APP_API_BASE_URL}/api` || 'http://localhost:8080'; 
  // 認証関連のエンドポイント
  static AUTH = {
    LOGIN: `${this.BASE_URL}/login`,
    LOGOUT: `${this.BASE_URL}/logout`,
    REGISTER: `${this.BASE_URL}/register`,
  };

  // ユーザー関連のエンドポイント
  static USER = {
    PROFILE: `${this.BASE_URL}/user/profile`,
  };

  // 書籍関連のエンドポイント
  static BOOKS = {
    LIST: `${this.BASE_URL}/books`,
    CREATE: `${this.BASE_URL}/books`,
    UPDATE: (id) => `${this.BASE_URL}/books/${id}`,
    DELETE: (id) => `${this.BASE_URL}/books/${id}`,
    SEARCH: `${this.BASE_URL}/books/search`,
    BORROWED: (userId) => `${this.BASE_URL}/books/borrowed/${userId}`,
    RESERVED: (userId) => `${this.BASE_URL}/books/reserved/${userId}`,
    VALIDATE_ISBN: `${this.BASE_URL}/books/validate/isbn`,
  };

  // 貸出関連のエンドポイント
  static RESERVATIONS = {
    CREATE: `${this.BASE_URL}/reservations`,
    RETURN: () => `${this.BASE_URL}/reservations/user`,
  };
} 