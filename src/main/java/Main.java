import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;
import vn.bcy.vgca.simtoolkit.VGCAToolkit;


import java.io.File;
import java.nio.file.Files;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PhanHoang
 * 7/11/2020
 */
// hashchain  QDeN7wj9dLRcjsGURhOYDvR5NpDPdT7HdT9UN8LfkSI=
public class Main {
    public static void main(String[] args) throws Exception {
        String cert = VGCAToolkit.GetCert("84705392218");
        File file = new File("C:/Users/phann/Desktop/bcy.JPG");
        byte[] image = Files.readAllBytes(file.toPath());

        File fileFont = new File("E:/MOBIFONE/eoffice-revo-sign-service/src/main/resources/share/font/times.ttf");
        byte[] fileFontFile = Files.readAllBytes(fileFont.toPath());
        List<String> certList = new ArrayList<>();
        certList.add("MIIFuzCCBKOgAwIBAgIDLvEdMA0GCSqGSIb3DQEBBQUAMFYxCzAJBgNVBAYTAlZOMR0wGwYDVQQKDBRCYW4gQ28geWV1IENoaW5oIHBodTEoMCYGA1UEAwwfQ28gcXVhbiBjaHVuZyB0aHVjIHNvIENoaW5oIHBodTAeFw0xNjA5MjkwNzIyNTdaFw0yNjA5MjcwNzIyNTdaMF8xCzAJBgNVBAYTAlZOMSMwIQYDVQQKDBpCYW4gQ8ahIHnhur91IENow61uaCBwaOG7pzESMBAGA1UEBwwJSMOgIE7hu5lpMRcwFQYDVQQDDA5Nb2JpbGUgVGVzdCAwMzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKJ7J449Ml+OxqAt5dZZtfcoi6AxZ3GUL0g8/XR/czEpVj55Ux1KjaBe5EHYAp6LXtpob4EQcKtVHL+A/90PsBj0gxijYdW9P/9uOVR+R3xcopAY+IBJsU4KMByK4fZAiv9Jull3T1XfZWfyPyXmg1vNa3jnDCr6Praq1+pinwpp8y1JqwvkRyCD23mn8+gA0rW9lszvAjS15iqISUmVLRieZ+u3bz+lFttqpoCW9N5p8AvNk/afjbjVM8/sukAdQG7/2eHhLcLFwfas6oAR2ejkDOGma8hvq53NCB7N33tIHe5RT9eIDVNQElMin05g86VJyddnPHC+r7KpDZ3wKoUCAwEAAaOCAocwggKDMAkGA1UdEwQCMAAwEQYJYIZIAYb4QgEBBAQDAgWgMAsGA1UdDwQEAwIE8DApBgNVHSUEIjAgBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcUAgIwJQYJYIZIAYb4QgENBBgWFlVzZXIgU2lnbiBvZiBDaGluaCBwaHUwHQYDVR0OBBYEFDGtY9+JyBDchixHcBgQu5KJKLWgMIGVBgNVHSMEgY0wgYqAFAUxQN40vrOPwNtuxUMOPhL3Y8YcoW+kbTBrMQswCQYDVQQGEwJWTjEdMBsGA1UECgwUQmFuIENvIHlldSBDaGluaCBwaHUxPTA7BgNVBAMMNENvIHF1YW4gY2h1bmcgdGh1YyBzbyBjaHV5ZW4gZHVuZyBDaGluaCBwaHUgKFJvb3RDQSmCAQQwGgYDVR0RBBMwEYEPdGVzdDNAY2EuZ292LnZuMDIGCWCGSAGG+EIBBAQlFiNodHRwOi8vY2EuZ292LnZuL3BraS9wdWIvY3JsL2NwLmNybDAyBglghkgBhvhCAQMEJRYjaHR0cDovL2NhLmdvdi52bi9wa2kvcHViL2NybC9jcC5jcmwwYwYDVR0fBFwwWjApoCegJYYjaHR0cDovL2NhLmdvdi52bi9wa2kvcHViL2NybC9jcC5jcmwwLaAroCmGJ2h0dHA6Ly9wdWIuY2EuZ292LnZuL3BraS9wdWIvY3JsL2NwLmNybDBkBggrBgEFBQcBAQRYMFYwIgYIKwYBBQUHMAGGFmh0dHA6Ly9vY3NwLmNhLmdvdi52bi8wMAYIKwYBBQUHMAKGJGh0dHA6Ly9jYS5nb3Yudm4vcGtpL3B1Yi9jZXJ0L2NwLmNydDANBgkqhkiG9w0BAQUFAAOCAQEARUuldTzzFFvFUbIgtWgQz0/uoE4JFISStXSJhq5B0LaJ+sOWot7f/p6h/9ktoXU141DIXuVbz3nxxadl9KN1XWcxGln+WSl/I0AMnBzRD4IMCcBApkldX5j1y5r8avqaZN406PfAlhnHba7QPNC7O8mKE3rV1syAsXClIhGxzBYi12AeFNdyZ10/gwNu6QkeppFDRhc34sAfh1XaNMxGt/t8MzHbUuUQzJh5b8eR7e2SqfAnsHKF1+msQ/cuhVIbtmCU0an0JulFN3lADRFzpKhQ/hGnhZHEwl2rmZFP2Bx3zB+u2EpscUxwHcWmyYNgAe+MOgj3rEpwMa/qSsUAyw==");

        X509Certificate[] certChain = X509ExtensionUtil.getCertChainOfCert(certList.get(0), null);

        String fileSource = "C:/Users/phann/Desktop/LOGGING/hoangtestbcy.pdf";
        String fileDes = "C:/Users/phann/Desktop/LOGGING/bcy";
        System.out.println(cert);
        Rectangle rectangle = new Rectangle(2, 2);
        TSAClient tsaClient = new TSAClientBouncyCastle("http://cms.ca.gov.vn:18443/apws.asmx","https://mpki.ca.gov.vn/mobifone", "kzkjK6sK");
        VGCAToolkit.signPDF(fileSource, fileDes, "84705392218", rectangle, 2, image, fileFontFile, certChain, tsaClient);


    }
}
