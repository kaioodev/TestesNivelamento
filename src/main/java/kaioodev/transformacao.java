package kaioodev;

import com.opencsv.CSVWriter;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class transformacao {

    public static void main(String[] args) {
        String caminhoPDF = "anexos/Anexo_I_Rol_2021RN_465.2021_RN627L.2024.pdf";
        String nomeCSV = "saida.csv";
        String nomeZIP = "Teste_kaio.zip";

        try {
            // 1. Abrir o PDF
            PDDocument documento = PDDocument.load(new File(caminhoPDF));
            ObjectExtractor extrator = new ObjectExtractor(documento);
            SpreadsheetExtractionAlgorithm algoritmo = new SpreadsheetExtractionAlgorithm();
            CSVWriter writer = new CSVWriter(new FileWriter(nomeCSV));

            // 2. Iterar páginas
            for (int i = 1; i <= documento.getNumberOfPages(); i++) {
                Page pagina = extrator.extract(i);
                List<Table> tabelas = algoritmo.extract(pagina);

                for (Table tabela : tabelas) {
                    List<List<RectangularTextContainer>> linhas = tabela.getRows();

                    for (List<RectangularTextContainer> linha : linhas) {
                        String[] valores = linha.stream()
                                .map(celula -> substituirSiglas(celula.getText()))
                                .toArray(String[]::new);
                        writer.writeNext(valores);
                    }
                }
            }

            writer.close();
            documento.close();

            // 3. Compactar em .zip
            try (FileOutputStream fos = new FileOutputStream(nomeZIP);
                 ZipOutputStream zipOut = new ZipOutputStream(fos);
                 FileInputStream fis = new FileInputStream(nomeCSV)) {

                ZipEntry zipEntry = new ZipEntry(nomeCSV);
                zipOut.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) >= 0) {
                    zipOut.write(buffer, 0, len);
                }
            }

            System.out.println("Transformação finalizada com sucesso");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String substituirSiglas(String texto) {
        return texto.replace("OD", "Consulta Odontológica")
                .replace("AMB", "Consulta Ambulatorial");
    }

}
