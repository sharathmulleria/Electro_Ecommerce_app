package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.repository.UserRepository;
import com.shopping.electroshopping.service.SalesReportService.PDFGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;

@Controller
@RequestMapping("/user")
public class UserDownloadPage {

    @Autowired
    private PDFGenerationService pdfGenerationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/downloadInvoice/{id}")
    public void downloadInvoice(@PathVariable Long id, HttpServletResponse response) throws IOException {

//        try {
//            byte[] pdfBytes = pdfGenerationService.generatePdf(id);
//
//            // Set the response headers for PDF download
//            response.setContentType("application/pdf");
//            response.setHeader("Content-Disposition", "attachment; filename=invoice.pdf");
//
//            // Write the PDF bytes to the response output stream
//            response.getOutputStream().write(pdfBytes);
//            response.getOutputStream().flush();
//
//        } catch (Exception e) {
//
//            // Handle any exceptions (e.g., template processing error)
//            e.printStackTrace();
//
//            // You can redirect to an error page or provide an error response
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.setContentType("text/plain");
//
//            response.getWriter().write("Error to generate pdf file. Enter Correct start and end date.!!");
//
//        }

        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename = Purchase_Invoice.pdf";
        response.setHeader(headerKey, headerValue);
        pdfGenerationService.generatePdf(id, response);



    }





}
