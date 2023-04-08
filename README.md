# API SHOP
- Cung cấp các API cho khách hàng: Xem cửa hàng, sản phẩm, mua hàng, quản lý 
  giỏ hàng, thông tin cá nhân,... 
- Cung cấp các API cho admin: Quản lý sản phẩm, đơn hàng, khách hàng, báo cáo thống kê,..
- Sử dụng Java Spring Boot, SQL Server, JWT Token, Jasper Report, Mail Sender,...
# Contributors
- `AnhJun18` -  `Lê Phương Anh`
#Release
#####Demo:  https://pa-shop-service.azurewebsites.net/swagger-ui.html
  
    API được deloy trên Azure với dịch vụ service free nên có 
    thế sẽ gặp lỗi không mong muốn.
    Hãy cài đặt ứng dụng để trải nghiệm tốt hơn


####FRONT END
#####Web (`ReactJS`):
   - Demo: https://shop-ptit.vercel.app/
   - Src: https://github.com/AnhJun18/Shop-Web-React


#####APP (`React Native`):
- Src: https://github.com/AnhJun18/shop-ptit-react-native
# Account test
- ADMIN:  `admin`- pass: 123
- USER:   `pa`   - pass: 12

# Installation
- Bước 1: Cài đặt Docker
- Bước 2: Build image
  + mở project bằng terminal
  + Chạy: `docker build -t pa-shop .`
  
- Bước 3: Run
  
  Chạy: `docker run -p 8081:8081 pa-shop`
- Bước 4: Mở Brower > Vào link: 
  http://localhost:8081/swagger-ui.html

# Screenshot
![](https://user-images.githubusercontent.com/81857289/230708435-94219e7a-910b-49d1-a330-d847071b4c2e.png)
![](https://user-images.githubusercontent.com/81857289/230708534-369f70c8-9ed1-4f10-bbf5-8201a6847a2b.png)
![](https://user-images.githubusercontent.com/81857289/230708579-43bbfb73-a1cd-4eea-85d7-5a325bf8c2f8.png)
![](https://user-images.githubusercontent.com/81857289/230708613-a283dbc4-2cc4-4112-aeaa-3130875a93ac.png)
![](https://user-images.githubusercontent.com/81857289/230708983-2f069644-6fb6-4945-ab8d-302f03a9e990.png)