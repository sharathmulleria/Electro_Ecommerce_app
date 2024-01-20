package com.shopping.electroshopping.service.SalesReportService;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.shopping.electroshopping.dto.SalesReportDTO;
import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.Order.OrderItem;
import com.shopping.electroshopping.model.accounts.Accounts;
import com.shopping.electroshopping.repository.OrderRepository;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PDFGenerationServiceImpl implements PDFGenerationService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TemplateEngine templateEngine;


    public void generate(List<Order> orders, HttpServletResponse response,
                         String startDate, String endDate) throws DocumentException, IOException {
        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);

        List<Order> orderList = orderRepository.findAll().stream().filter(order -> order.getOrdered_date()
                .isAfter(startLocalDate.minusDays(1)) && order.getOrdered_date()
                .isBefore(endLocalDate.plusDays(1))).collect(Collectors.toList());

        Map<LocalDate, List<Order>> ordersByDate = orderList.stream()
                .collect(Collectors.groupingBy(Order::getOrdered_date));


        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 35, Font.BOLD);
        fontTitle.setSize(20);
        Paragraph paragraph1 = new Paragraph("Electro Sales Report "+startDate +" to "+ endDate);
        paragraph1.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph1);
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 3, 3});
        table.setSpacingBefore(5);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(CMYKColor.LIGHT_GRAY);
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);
        cell.setPhrase(new Phrase("Orders", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Ordered Date", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Order Amount", font));
        table.addCell(cell);

        for (Map.Entry<LocalDate, List<Order>> entry : ordersByDate.entrySet()){

            List<Order> ordersOnDate = entry.getValue();
            LocalDate date = entry.getKey();
            int ordersTotal = ordersOnDate.size();
            float sum = (float) ordersOnDate.stream().mapToDouble(Order::getTotal).sum();

            table.addCell(String.valueOf(ordersTotal));
            table.addCell(String.valueOf(date));
            table.addCell(String.valueOf(sum));

        }
        document.add(table);
        document.close();

    }





        public byte[] generatePdf(Long id) throws IOException{
        // Create a Thymeleaf context and add any dynamic data needed in your template
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // Create a Thymeleaf context and add the Order object as a variable
            Context context = new Context();
            context.setVariable("order", order);

            // Process the Thymeleaf template to generate HTML content
            String htmlContent = templateEngine.process("invoiceDownload", context);

            // Generate the PDF from the HTML content
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(outputStream);

                return outputStream.toByteArray();
            }

        } else {
            // Handle the case where the order is not found (e.g., throw an exception or return an error response)
            throw new EntityNotFoundException("Order not found with ID: " + id);
        }
    }


    @Override
    public byte[] generateSalesReportExcel(List<SalesReportDTO> salesData) throws IOException {
        // Create an instance of Workbook (e.g., Apache POI XSSFWorkbook for XLSX)
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sales Report");

        // Create the header row
        XSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Product");
        headerRow.createCell(1).setCellValue("Quantity Sold");
        headerRow.createCell(2).setCellValue("Total Sales");
        headerRow.createCell(3).setCellValue("Order Summary");

        // Fill in the data
        for (int i = 0; i < salesData.size(); i++) {
            SalesReportDTO sale = salesData.get(i);
            XSSFRow dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(sale.getProductName());
            dataRow.createCell(2).setCellValue(sale.getTotalSales());
            dataRow.createCell(3).setCellValue("Order ID: " + sale.getOrderId() + "\nOrder Date: " + sale.getOrderDate() + "\nOrder Total: " + sale.getOrderTotal());
        }

        // Create a ByteArrayOutputStream to store the Excel data
        ByteArrayOutputStream excelStream = new ByteArrayOutputStream();
        workbook.write(excelStream);
        workbook.close();

        return excelStream.toByteArray();
    }


    public List<SalesReportDTO> getSalesReportData() {

        List<SalesReportDTO> salesData = new ArrayList<>();
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {

            for (OrderItem orderItem : order.getOrderItems()) {
                SalesReportDTO salesReport = new SalesReportDTO();

                // Add null check for product
                if (orderItem.getProduct() != null) {
                    salesReport.setProductName(orderItem.getProduct().getProductName());
                } else {
                    salesReport.setProductName("Unknown Product");
                }


                salesReport.setTotalSales(orderItem.getQuantity() * orderItem.getPrice());
                salesReport.setOrderId(order.getId());
                salesReport.setOrderDate(order.getOrdered_date());
                salesReport.setOrderTotal(orderItem.getQuantity() * orderItem.getPrice());


                salesData.add(salesReport);
            }
        }
        return salesData;
    }


}
