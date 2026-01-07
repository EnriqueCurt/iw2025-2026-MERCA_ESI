package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Cliente;
import com.example.iw20252026merca_esi.model.ItemPedido;
import com.example.iw20252026merca_esi.model.Pedido;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TicketPdfService {

    public byte[] generarTicketPdf(Pedido pedido, Cliente cliente, List<ItemPedido> items) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Color corporativo
            DeviceRgb colorRojo = new DeviceRgb(227, 6, 19);

            // Título
            Paragraph titulo = new Paragraph("MERCA ESI")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(colorRojo)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Ticket de Compra")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(subtitulo);

            // Información del pedido
            document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    .setFontColor(colorRojo));

            document.add(new Paragraph("Nº Pedido: " + pedido.getIdPedido())
                    .setBold()
                    .setFontSize(12));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            document.add(new Paragraph("Fecha: " + pedido.getFecha().format(formatter))
                    .setFontSize(10));

            if (cliente != null) {
                document.add(new Paragraph("Cliente: " + cliente.getNombre())
                        .setFontSize(10));
            }

            String tipoEntrega = pedido.getADomicilio() ? "A Domicilio" :
                                (pedido.getParaLlevar() ? "Para Llevar" : "En Local");
            document.add(new Paragraph("Tipo de entrega: " + tipoEntrega)
                    .setFontSize(10));

            if (pedido.getDireccion() != null && !pedido.getDireccion().isEmpty()) {
                document.add(new Paragraph("Dirección: " + pedido.getDireccion())
                        .setFontSize(10));
            }

            document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    .setFontColor(colorRojo)
                    .setMarginBottom(15));

            // Tabla de productos
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold())
                    .setBackgroundColor(colorRojo)
                    .setFontColor(ColorConstants.WHITE));
            table.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold())
                    .setBackgroundColor(colorRojo)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio").setBold())
                    .setBackgroundColor(colorRojo)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setBold())
                    .setBackgroundColor(colorRojo)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.RIGHT));

            // Filas de productos
            for (ItemPedido item : items) {
                table.addCell(new Cell().add(new Paragraph(item.getNombre())
                        .setFontSize(10)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getCantidad()))
                        .setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f €", item.getPrecio()))
                        .setFontSize(10))
                        .setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f €", item.getSubtotal()))
                        .setFontSize(10))
                        .setTextAlignment(TextAlignment.RIGHT));

                // Si hay exclusiones, mostrarlas
                if (item.tieneExclusiones()) {
                    String exclusiones = "   " + item.getTextoExclusiones();
                    table.addCell(new Cell(1, 4)
                            .add(new Paragraph(exclusiones)
                            .setFontSize(8)
                            .setItalic()
                            .setFontColor(ColorConstants.DARK_GRAY)));
                }
            }

            document.add(table);

            // Total
            document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    .setFontColor(colorRojo)
                    .setMarginTop(15));

            Paragraph total = new Paragraph("TOTAL: " + String.format("%.2f €", pedido.getTotal()))
                    .setFontSize(18)
                    .setBold()
                    .setFontColor(colorRojo)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(total);

            document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    .setFontColor(colorRojo));

            // Pie de página
            document.add(new Paragraph("\n¡Gracias por su compra!")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20));

            document.add(new Paragraph("MERCA ESI - Su restaurante de confianza")
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }
}

