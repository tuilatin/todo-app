import { useState, useEffect } from "react";
import axios from "axios";
import "./App.css";

// 🌟 Định nghĩa URL chạy về Back-end Spring Boot của bạn
const BASE_URL = "http://localhost:8080";
const AUTH_URL = `${BASE_URL}/api/v1/auth/login`;
const API_URL = `${BASE_URL}/api/todos`;
const API_URLCategory = `${BASE_URL}/api/categories`;

function App() {
  // --- 1. QUẢN LÝ TRẠNG THÁI (Thay cho việc quản lý DOM kiểu cũ) ---
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const [todos, setTodos] = useState([]);
  const [categories, setCategories] = useState([]);
  const [todoInput, setTodoInput] = useState("");
  const [addTodoCategory, setAddTodoCategory] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [authError, setAuthError] = useState("");

  // Trạng thái tìm kiếm và bộ lọc
  const [todoSearch, setTodoSearch] = useState("");
  const [listCategorySelect, setListCategorySelect] = useState("");
  const [filterStatus, setFilterStatus] = useState("");

  // Trạng thái phục vụ tính năng SỬA (startEdit, cancelEdit)
  const [editingId, setEditingId] = useState(null); // Lưu ID của todo đang được sửa
  const [editTitle, setEditTitle] = useState(""); // Lưu chữ đang gõ trong ô sửa

  const authHeaders = token ? { Authorization: `Bearer ${token}` } : {}; 

  // --- 2. CÁC HÀM GỌI API (Bê nguyên logic từ app.js cũ của bạn sang Axios) ---

  // Tương đương: loadTodos()
  const loadTodos = async () => {
    try {
      const res = await axios.get(API_URL, { headers: authHeaders });
      setTodos(res.data); // Nhét dữ liệu vào State, React sẽ tự vẽ lại giao diện
    } catch (err) {
      console.error("Lỗi tải todos:", err);
    }
  };

  // Tương đương: loadCategories()
  const loadCategories = async () => {
    try {
      const res = await axios.get(API_URLCategory, { headers: authHeaders });
      setCategories(res.data);
    } catch (err) {
      console.error("Lỗi tải categories:", err);
    }
  };

  // Tự động chạy khi trang web vừa mở lên
  useEffect(() => {
    if (token) {
      loadTodos();
      loadCategories();
    }
  }, [token]);

  const login = async () => {
    try {
      const res = await axios.post(AUTH_URL, {
        username: username.trim(),
        password: password.trim(),
      });
      const jwt = res.data.token;
      localStorage.setItem("token", jwt);
      setToken(jwt);
      setAuthError("");
      setUsername("");
      setPassword("");
    } catch (err) {
      console.error("Lỗi đăng nhập:", err);
      setAuthError("Đăng nhập thất bại. Vui lòng kiểm tra tên đăng nhập và mật khẩu.");
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken("");
    setTodos([]);
    setCategories([]);
    setFilterStatus("");
    setListCategorySelect("");
    setTodoInput("");
    setEditTitle("");
    setEditingId(null);
  };

  // Tương đương: addTodo()
  const addTodo = async () => {
    if (!todoInput.trim()) {
      alert("Vui lòng nhập todo!");
      return;
    }
    await axios.post(API_URL, {
      title: todoInput,
      categoryId: addTodoCategory,
      completed: false,
    }, { headers: authHeaders });
    setTodoInput(""); // Xóa chữ trong ô nhập
    loadTodos(); // Load lại danh sách
  };

  // Tương đương: deleteTodo(id)
  const deleteTodo = async (id) => {
    await axios.delete(`${API_URL}/${id}`, { headers: authHeaders });
    loadTodos();
  };

  // Tương đương: toggleTodo(id, completed, title)
  const toggleTodo = async (id, completed, title) => {
    await axios.put(`${API_URL}/${id}`, {
      title: title,
      completed: !completed,
    }, { headers: authHeaders });
    loadTodos();
  };

  // Tương đương: startEdit(id) và saveEdit(id)
  const startEdit = (id, currentTitle) => {
    setEditingId(id); // Đánh dấu dòng này đang sửa (React sẽ tự hiện ô input)
    setEditTitle(currentTitle); // Điền chữ cũ vào ô input
  };

  const cancelEdit = () => {
    setEditingId(null); // Hủy sửa, giao diện tự quay về dạng chữ thường
  };

  const saveEdit = async (id) => {
    if (!editTitle.trim()) {
      alert("Vui lòng nhập todo!");
      return;
    }
    await axios.put(`${API_URL}/${id}`, {
      title: editTitle,
      completed: false,
    }, { headers: authHeaders });
    setEditingId(null);
    loadTodos();
  };

  // Tương đương: filterByStatus() kết hợp với nút lọc thay đổi
  const handleStatusChange = async (e) => {
    const status = e.target.value;
    setFilterStatus(status);
    if (status === "") {
      await loadTodos();
    } else {
      const res = await axios.get(`${API_URL}/search?completed=${status}`, { headers: authHeaders });
      setTodos(res.data);
    }
  };

  // Tương đương: searchTodos() (Đã được làm sạch sẽ hơn bài toán cũ của bạn)
  const searchTodos = async () => {
    if (listCategorySelect === "" && todoSearch === "") {
      await loadTodos();
      return;
    }
    let url = `${API_URL}/search?`;
    if (todoSearch.trim()) url += `keyword=${todoSearch}&`;
    if (listCategorySelect) url += `categoryId=${listCategorySelect}`;

    const res = await axios.get(url, { headers: authHeaders });
    setTodos(res.data);
  };

  // --- 3. PHẦN GIAO DIỆN (Trộn lẫn index.html và renderTodos của app.js cũ) ---
  return (
    <div style={{ padding: "20px" }}>
      {!token ? (
        <div>
          <h1>Đăng nhập</h1>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button onClick={login}>Đăng nhập</button>
          {authError && <p style={{ color: "red" }}>{authError}</p>}
        </div>
      ) : (
        <div>
          <div style={{ marginBottom: "20px" }}>
            <strong>Đã đăng nhập</strong>
            <button style={{ marginLeft: "10px" }} onClick={logout}>
              Đăng xuất
            </button>
          </div>

          <h1>Todo List (React Version)</h1>

          {/* KHỐI THÊM TODO (Giống index.html cũ) */}
          <input
            type="text"
            placeholder="Nhập todo..."
            value={todoInput}
            onChange={(e) => setTodoInput(e.target.value)}
          />
          <select
            value={addTodoCategory}
            onChange={(e) => setAddTodoCategory(e.target.value)}
          >
            <option value="">Chọn thể loại</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.title}
              </option>
            ))}
          </select>
          <button onClick={addTodo}>Thêm</button>

          <hr />

          {/* KHỐI TÌM KIẾM & BỘ LỌC */}
          <input
            type="text"
            placeholder="Tên todo cần tìm..."
            value={todoSearch}
            onChange={(e) => setTodoSearch(e.target.value)}
          />
          <select
            value={listCategorySelect}
            onChange={(e) => setListCategorySelect(e.target.value)}
          >
            <option value="">Tất cả thể loại</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.title}
              </option>
            ))}
          </select>

          <select value={filterStatus} onChange={handleStatusChange}>
            <option value="">Tất cả trạng thái</option>
            <option value="true">Hoàn thành</option>
            <option value="false">Đang làm</option>
          </select>

          <button onClick={searchTodos}>Tìm</button>
          <button onClick={loadTodos}>Reload</button>

          <p>Tổng số công việc: {todos.length}</p>
          <p style={{ fontWeight: "bold" }}>
            ID | Tên công việc | Đã xong chưa | Thể loại
          </p>

          {/* KHỐI DANH SÁCH TODO (Thay thế cho hàm renderTodos cộng chuỗi cũ) */}
          <ul id="todoList">
            {todos.map((todo) => {
              const isCurrentEditing = editingId === todo.id; // Kiểm tra dòng này có đang bấm Sửa không

              return (
                <li key={todo.id} style={{ marginBottom: "8px" }}>
                  <span>{todo.id} </span>

                  <input
                    type="checkbox"
                    checked={todo.completed}
                    onChange={() => toggleTodo(todo.id, todo.completed, todo.title)}
                  />

                  {/* Ẩn hiện ô Input viết cực kỳ gọn bằng toán tử ba ngôi của React */}
                  {isCurrentEditing ? (
                    <input
                      type="text"
                      value={editTitle}
                      onChange={(e) => setEditTitle(e.target.value)}
                    />
                  ) : (
                    <span
                      style={{
                        textDecoration: todo.completed ? "line-through" : "none",
                        marginRight: "10px",
                      }}
                    >
                      {todo.title}{" "}
                      <strong>({todo.categoryName || "Không có"})</strong>
                    </span>
                  )}

                  {/* Nhóm nút bấm điều khiển */}
                  {isCurrentEditing ? (
                    <>
                      <button onClick={() => saveEdit(todo.id)}>Lưu</button>
                      <button onClick={cancelEdit}>Hủy</button>
                    </>
                  ) : (
                    <>
                      <button onClick={() => startEdit(todo.id, todo.title)}>
                        Sửa
                      </button>
                      <button onClick={() => deleteTodo(todo.id)}>Xóa</button>
                    </>
                  )}
                </li>
              );
            })}
          </ul>
        </div>
      )}
    </div>
  );
}

export default App;
