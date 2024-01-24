package com.shopping.electroshopping.service.SalesReportService;

import com.lowagie.text.DocumentException;
import com.shopping.electroshopping.dto.SalesReportDTO;
import com.shopping.electroshopping.model.Order.Order;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface PDFGenerationService {


/* Generate Sales report based on Start date and End date.*/
    public void generate(List<Order> orders, HttpServletResponse response,
                         String startDate, String endDate)
                            throws DocumentException, IOException;


/* Download user order Invoice in the User side. It will
   Download all the user order in pdf   */
    public void generatePdf(Long id, HttpServletResponse response) throws IOException;


/* Generate Excel Document for Sales report. It will Accept Start date and
   End date.*/
    byte[] generateSalesReportExcel(List<SalesReportDTO> salesData) throws IOException;
}
