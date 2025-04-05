package kaioodev;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.io.FileUtils;
import net.lingala.zip4j.ZipFile;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class webscrapping {
    public static void main(String[] args) {
        String url = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
        String downloadDir = "anexos/";

        try {
            // Acessa o HTML do site
            Document doc = Jsoup.connect(url).get();

            // Filtra pelos PDFs da página
            Elements pdfLinks = doc.select("a[href$=.pdf]");

            // Criar diretório para os downloads
            new File(downloadDir).mkdirs();

            // 4. Fazer download dos Anexos I e II
            for (Element link : pdfLinks) {
                String pdfUrl = link.attr("abs:href");
                String fileName = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);

                // Filtra apenas Anexo I e II
                if (fileName.contains("Anexo_I") || fileName.contains("Anexo_II")) {
                    System.out.println("Downloading: " + fileName);
                    FileUtils.copyURLToFile(new URL(pdfUrl), new File(downloadDir + fileName));
                }
            }

            // Compacta os arquivos em um .ZIP
            String zipFileName = "anexos_ans.zip";
            new ZipFile(zipFileName).addFolder(new File(downloadDir));
            System.out.println("Arquivos compactados em: " + zipFileName);

        } catch (IOException e) {
            System.err.println("Erro durante a execução:");
            e.printStackTrace();
        }
    }
}