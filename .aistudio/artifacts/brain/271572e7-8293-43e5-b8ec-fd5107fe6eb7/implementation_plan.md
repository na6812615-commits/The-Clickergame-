# TapCore VN — Neon Arcade Incremental Clicker

Tựa game Android Jetpack Compose lấy cảm hứng trực tiếp từ file game clicker Python gốc, nâng cấp thành trải nghiệm **Neon Arcade** trực quan với cơ chế chạm tính hồi chiêu, hai cửa hàng Điểm & Đô-la (`$Shop$`), hệ thống Nâng cấp Auto-Click hoàn chỉnh, Tái sinh (`Restarts`) tăng hệ số nhân vĩnh viễn và lưu tiến trình đám mây qua Firebase Firestore.

---

## User Review & Critical Decisions

> [!IMPORTANT] Vì các câu hỏi làm rõ đã được bỏ qua, bản kế hoạch sử dụng các lựa chọn mặc định tối ưu dưới đây. Vui lòng xem lại và bấm **Proceed** (hoặc yêu cầu chỉnh sửa) trước khi bắt đầu tạo mã nguồn.

- **Phong cách giao diện (Mặc định khuyến nghị)**: **Neon Arcade tối hiện đại (Dark Cyber-Terminal)** — nền tối sâu kết hợp bảng số liệu dạng monospace phát sáng Neon Cyan, Emerald và Amber Gold để giữ trọn tinh thần terminal của file gốc nhưng hiện đại và dễ thao tác trên màn hình cảm ứng.
- **Mục "Nâng cấp Auto Click"**: **Mở khóa tính năng thật** thay vì giữ thông báo *"coming soon..."*, bổ sung hai nhánh nâng cấp bằng `$` là **Tăng điểm Auto** và **Giảm chu kỳ Auto**.
- **Lưu trữ tiến trình**: **Lưu đám mây bằng Firebase Firestore kèm Đăng nhập Google** để điểm, tiền `$`, cấp độ nâng cấp và cấp `Restarts` được bảo toàn xuyên suốt khi đổi máy hoặc cài lại ứng dụng.
- **Ngôn ngữ hiển thị**: **Tiếng Việt có dấu đầy đủ**, rõ ràng và thân thiện với người dùng Android.

---

## 1. Overview & Core Concept

- **What It Does**: Người chơi chạm vào lõi năng lượng trung tâm để tích lũy **Điểm** (`diem`) theo thời gian hồi (`cd`), dùng Điểm mua nâng cấp trong **Cửa hàng Điểm**, quy đổi `1.000 Điểm = $1` để mua các đặc quyền trong **`$Shop$`** (giảm giá toàn bộ cửa hàng, tăng tốc thông báo/chuyển cảnh, mở khóa và nâng cấp **Auto Click**), và thực hiện **Restarts (Tái sinh)** để nhận hệ số nhân khởi điểm vĩnh viễn cao hơn.
- **Target Audience / Persona**: Người chơi yêu thích thể loại incremental / idle clicker nhẹ nhàng, muốn theo dõi các con số tăng trưởng cấp số nhân và tối ưu chiến lược nâng cấp giữa các vòng tái sinh.
- **Key Value**: Giữ nguyên toàn bộ công thức toán học và nhịp điệu cân bằng từ kịch bản Python gốc (hệ số `giamgia`, chuỗi nâng cấp nhân 3 giá, cooldown giảm dần một nửa), đồng thời loại bỏ thao tác gõ phím lặp lại bằng giao diện cảm ứng mượt mà, phản hồi xúc giác và đồng bộ đám mây.

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Màn hình Chào & Đăng nhập Google**: Người chơi mở app, thử tự động đăng nhập im lặng nếu đã liên kết trước đó; nếu chưa, hiển thị màn hình Neon Arcade với nút **Đăng nhập bằng Google** để tải hoặc khởi tạo hồ sơ game.
  2. **Màn hình Chơi chính (`Tap Core`)**:
     - Thanh thống kê **STATS** ghim ở phía trên hiển thị trực tiếp: `Điểm hiện tại`, `Tiền $`, `Hệ số nhân (x)`, `Điểm / click`, `Tổng lượt click`, và `Thời gian hồi / click (s)`.
     - Lõi chạm trung tâm cỡ lớn kèm vòng tròn đếm ngược hồi chiêu (`cd`), hiệu ứng nảy lò xo khi chạm, số `+điểm` bay lên và huy hiệu trạng thái **Auto Click** khi đã kích hoạt.
  3. **Cửa hàng Điểm (`Shop`)**: 3 thẻ nâng cấp dùng Điểm (áp dụng giảm giá `1 / giamgia`):
     - `+x Điểm mỗi click` (nhân đôi `x`, giá khởi điểm 25 điểm, tăng x3 mỗi cấp).
     - `Thêm Hệ số nhân` (`+0.1x` vào `muti`, giá khởi điểm 50 điểm, tăng x3 mỗi cấp).
     - `Giảm Cooldown Click` (giảm `cd` theo bước `thay` chia đôi dần, giá khởi điểm 75 điểm, tăng x3 mỗi cấp).
  4. **Đổi Điểm & `$Shop$`**:
     - Khu vực quy đổi nhanh: **Đổi 1.000 Điểm → $1** và **Đổi toàn bộ Điểm → $**.
     - 5 mục nâng cấp bằng `$`: **Giảm giá (`giamgia`)**, **Giảm thời gian chuyển cảnh (`cd3`)**, **Giảm thời gian thông báo (`cd4`)**, **Mở khóa Auto Click ($25)**, và mục **Nâng cấp Auto Click** (mở ra panel nâng cấp **Sức mạnh Auto `x2`** và **Tốc độ Auto `cd2`**).
  5. **Tái sinh (`Restarts`)**: Hiển thị cấp `lv restarts`, hệ số nhân khởi điểm hiện tại → kế tiếp (`muti2 → muti2 + 0.5`), thanh tiến độ điểm yêu cầu (`round(gia3 / giamgia)`), và hộp thoại xác nhận trước khi đặt lại vòng chơi.

- **Visual Identity & Theme**:
  - *Aesthetic Direction*: **Neon Arcade Cyber-Terminal** — tối giản, sắc nét, tập trung vào số liệu trực quan và cảm giác bấm "đã tay".
  - *Color Palette & Mood*: Nền Midnight Obsidian (`#0A0E17`), bề mặt thẻ Carbon Slate (`#121826` / `#1A2234`), điểm nhấn Electric Cyan (`#00F0FF`) cho Điểm & Tap Core, Neon Emerald (`#00E676`) cho Tiền `$` & Auto Click, Amber Gold (`#FFB300`) cho Hệ số nhân & Restarts, và Crimson (`#FF4D6D`) cho cảnh báo thiếu tài nguyên.
  - *Typography & Hierarchy*: Kết hợp **Space Grotesk** cho tiêu đề/nhãn nút, **JetBrains Mono** (dạng số thẳng hàng) cho toàn bộ bộ đếm STATS, giá nâng cấp và timer hồi chiêu, cùng **Plus Jakarta Sans** cho mô tả tiếng Việt dễ đọc.
  - *Component Styling & Layout*: Tuân thủ Material 3 với `enableEdgeToEdge()`, thanh điều hướng dưới (hoặc Navigation Rail trên màn hình rộng/tablet) gồm 4 tab chính: **Chơi**, **Shop Điểm**, **`$Shop$`**, và **Restarts**.

- **Interactive Feedback & Motion**:
  - Cooldown progress ring quanh nút Tap Core đồng bộ chính xác với `cd`.
  - Phản hồi rung nhẹ (`HapticFeedback`) khi chạm, mua nâng cấp hoặc tái sinh.
  - Banner thông báo nổi (toast) hiển thị kết quả mua/lỗi với thời lượng tự động thu gọn theo chỉ số `cd4`, và tốc độ chuyển tab/hộp thoại điều chỉnh theo `cd3`.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Mở khóa thật mục "Nâng cấp Auto Click"**
  - *Chosen Approach*: Khi người chơi mở mục Nâng cấp Auto Click trong `$Shop$`, hiển thị bảng nâng cấp **Điểm/lần Auto (`x2`)** và **Tốc độ Auto (`cd2`)** dùng `$` (yêu cầu đã mua Auto Click trước đó).
  - *Why*: Tránh để một nút bấm vô dụng ("dead-end UI") trong app Android hoàn chỉnh và tạo chiều sâu cho giai đoạn cuối vòng chơi.
  - *Alternatives Considered*: Giữ màn hình "Coming soon..." tĩnh giống file Python gốc, nhưng sẽ làm giảm giá trị trải nghiệm trên di động.

- **Decision 2: Lưu trữ đám mây với Firebase Firestore & Google Sign-In**
  - *Chosen Approach*: Sử dụng Firebase Authentication (Google Sign-In qua Jetpack Credential Manager) làm cổng xác thực và lưu trạng thái trò chơi theo `uid` trên Cloud Firestore.
  - *Why*: Bảo vệ toàn bộ công sức cày điểm và cấp độ `Restarts` của người chơi ngay cả khi đổi thiết bị hoặc cài lại ứng dụng, đồng thời tuân thủ kiến trúc bảo mật zero-trust.
  - *Alternatives Considered*: Lưu cục bộ bằng Room/DataStore, tuy đơn giản hơn nhưng dữ liệu sẽ mất khi gỡ ứng dụng.

- **Decision 3: Đồng bộ Firestore theo chu kỳ và sự kiện quan trọng**
  - *Chosen Approach*: Vòng lặp chạm và Auto-Click cập nhật trạng thái tức thì trong `StateFlow` trên bộ nhớ, còn thao tác ghi lên Firestore được thực hiện theo debounce (mỗi vài giây khi có thay đổi) và ghi ngay lập tức tại các mốc quan trọng (mua nâng cấp, đổi `$`, `Restarts`, tạm dừng ứng dụng).
  - *Why*: Tránh gửi hàng chục request mạng mỗi giây khi người chơi chạm liên tục hoặc bật Auto Click, giúp mượt máy và tiết kiệm quota Firestore.

- **Decision 4: Làm tròn & chống số âm cho `cd` và `diem`**
  - *Chosen Approach*: Áp dụng đúng công thức `cd -= thay; thay /= 2` nhưng giới hạn tối thiểu `cd >= 0.01s` và `diem >= 0` với định dạng hiển thị `round(diem)` / `2 chữ số thập phân` giống bản gốc.
  - *Why*: Đảm bảo trò chơi trung thành với cân bằng gốc nhưng không bị lỗi chia 0 hoặc hiển thị số thực dài dòng trên UI.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Component Diagram**:

```text
┌──────────────────────────────────────────────────────────────────────┐
│                       Presentation Layer (Compose)                   │
│  ┌──────────────┐   ┌──────────────────────────────────────────────┐ │
│  │  AuthScreen  │   │             MainGameShell (4 Tabs)           │ │
│  │ (Google Sign │──▶│  [TapCore]  [PointShop]  [CashShop]  [Rebirth] │
│  │  -In Gate)   │   └──────────────────────┬───────────────────────┘ │
└────────────────────────────────────────────┼─────────────────────────┘
                                             │ StateFlow & Events
┌────────────────────────────────────────────▼─────────────────────────┐
│                        Domain / State Layer                          │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │                        GameViewModel                           │  │
│  │  • Manual Tap & Cooldown Timer Coroutine                       │  │
│  │  • Auto-Clicker Worker Coroutine (cd2, x2 * muti)              │  │
│  │  • Shop Purchase, Currency Exchange & Prestige (Restarts)      │  │
│  │  • Debounced Cloud Save + Lifecycle Flush                      │  │
│  └─────────────────────────────┬──────────────────────────────────┘  │
└────────────────────────────────┼─────────────────────────────────────┘
                                 │ Suspend & Snapshot Flow
┌────────────────────────────────▼─────────────────────────────────────┐
│                         Data & Cloud Layer                           │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │                   GameProgressRepository                       │  │
│  │  • Auth-gated UID scoping (/users/{uid}/gameState/current)     │  │
│  │  • Custom Firestore DB ID (R.string.firestore_database_id)     │  │
│  │  • Structured Error Handling & Security Rules Enforcement      │  │
│  └────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
```

- **Data Model & State**:
  - Thực thể chính `PlayerGameState` lưu dưới `/users/{userId}/gameState/current` gồm:
    - Tài nguyên & Chỉ số: `userId`, `diem`, `dollars` (`do`), `clicks`, `multiplier` (`muti`), `baseMultiplier` (`muti2`), `pointsPerClick` (`x`), `clickCooldownSec` (`cd`), `cooldownStep` (`thay`), `discountFactor` (`giamgia`), `discountStep` (`thay2`).
    - Cấp độ & Giá Cửa hàng Điểm: `pointPowerLv` / `pointPowerCost`, `multiplierLv` / `multiplierCost`, `cooldownLv` / `cooldownCost`.
    - Cấp độ & Giá `$Shop$`: `discountLv` / `discountCost`, `menuSpeedLv` / `menuSpeedCost` (`cd3`), `toastSpeedLv` / `toastSpeedCost` (`cd4`), `autoEnabled`, `autoUnlocked` (`!gioihan`), `autoPoints` (`x2`), `autoCooldownSec` (`cd2`), `autoPowerLv` / `autoPowerCost`, `autoSpeedLv` / `autoSpeedCost`.
    - Tiến trình Tái sinh: `restartLv` (`lv3`), `restartCost` (`gia3`).
    - Dấu thời gian máy chủ: `@ServerTimestamp createdAt`, `@ServerTimestamp updatedAt`.

- **Interactive Component & State Mapping**:
  - *Auth Gate (`MainActivity` / `AppContent`)*: Theo dõi `FirebaseAuth.AuthStateListener`. Khi chưa đăng nhập, hiển thị `AuthScreen` (hỗ trợ tự động đăng nhập im lặng `GetGoogleIdOption` và nút bấm thủ công `GetSignInWithGoogleOption`). Khi đã đăng nhập, khởi tạo `GameViewModel` theo `uid`.
  - *Tap Core & Auto Worker*: `GameViewModel` quản lý timer hồi chiêu thủ công (`isCooldownActive`, `cooldownProgress`) và vòng lặp coroutine tự động (`while (autoEnabled)` cộng `x2 * muti` mỗi `cd2` giây).
  - *Shop & Exchange Handlers*: Mỗi nút nâng cấp kiểm tra điều kiện đủ tài nguyên (`diem >= round(cost / giamgia)` hoặc `dollars >= cost`), cập nhật ngay trạng thái UI, phát thông báo theo thời lượng `cd4`, và đánh dấu trạng thái cần đồng bộ lên Firestore.
  - *Security Rules & Verification*: Trước khi triển khai, thiết lập quy tắc bảo mật kiểm tra `request.auth != null`, `request.auth.uid == userId`, xác thực kiểu dữ liệu và biên giá trị hợp lệ cho toàn bộ trường của `PlayerGameState`, kiểm thử bằng cả bộ test JavaScript và Robolectric trên Emulator cục bộ trước khi đẩy quy tắc lên backend.
