package com.shopping.electroshopping.controllers.admin;

import com.shopping.electroshopping.dto.SalesReportDTO;
import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.repository.OrderRepository;
import com.shopping.electroshopping.service.SalesReportService.PDFGenerationService;
import com.shopping.electroshopping.service.SalesReportService.PDFGenerationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
public class AdminSalesReportController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private PDFGenerationService PDFGenerationService;

    @Autowired
    PDFGenerationServiceImpl pdfService;


    @GetMapping("/admin/salesReport")
    public void downloadSalesReport(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
        String currentDateTime = dateFormat.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Sales_Report" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        List<Order> orderList = orderRepository.findAll();
//        PDFGenerationServiceImpl generator = new PDFGenerationServiceImpl();
        pdfService.generate(orderList,response, startDate, endDate);
    }





    @GetMapping("/admin/salesReportExcel")
    public void downloadSalesReportExcel(HttpServletResponse response) throws IOException {
        try {


            List<SalesReportDTO> salesData = pdfService.getSalesReportData();

            byte[] excelBytes = pdfService.generateSalesReportExcel(salesData);


            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=salesReport.xlsx");
            // Write the PDF bytes to the response output stream
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            // Redirect to an error page or provide an error response
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


}
