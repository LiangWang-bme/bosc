# bosc

/frontend 文件夹是放置前端文件

/bank-receipt-serive 后端代码文件夹

src/main/java/
└── org.example.bankreceipt/
├── config/                # 配置类
├── controller/            # Spring MVC控制器
│   ├── DetailController.java
│   ├── QueryController.java
│   └── ReceiptController.java
├── dto/                   # 数据传输对象
│   ├── ApiResponse.java
│   └── ReceiptFileDTO.java
├── exception/             # 自定义异常
│   └── ResourceNotFoundException.java
├── model/                 # 数据模型/实体类
│   ├── BankReceipt.java   # 回单
│   └── Detail.java        # 明细
├── repository/            # 数据访问层
│   ├── DetailRepository.java
│   └── ReceiptRepository.java
├── service/               # 业务逻辑层
│   ├── DetailService.java
│   ├── FileSystemReceiptService.java
│   ├── PdfParserService.java
│   └── ReceiptService.java
└── BankReceiptApplication.java  # Spring Boot启动类