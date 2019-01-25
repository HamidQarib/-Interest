import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class ParserTheXmlFile {


    public static void main(String[] args) {

        try {
            String filePath = "main.xml";
            File xmlFile = new File ( filePath );
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder ();
            Document document = documentBuilder.parse ( xmlFile );
            document.getDocumentElement ().normalize ();
            NodeList nodeList = document.getElementsByTagName ( "deposit" );
            int shomarande = nodeList.getLength ();
            List<Deposit> deposits = new ArrayList <> (  );
            for (int i = 0; i < shomarande; i++) {
                Deposit deposit = null;
                try {
                    Node node = nodeList.item ( i );
                    Element element = null;
                    deposit = new Deposit ();
                    if (node.getNodeType () == Node.ELEMENT_NODE) {
                        element = (Element) node;
                        deposit.setCustomerNumber ( Integer.parseInt ( String.valueOf ( element.getElementsByTagName ( "customerNumber" ).item ( 0 ).getTextContent () ) ) );
                        deposit.setDepositBalance ( BigDecimal.valueOf ( Long.parseLong ( String.valueOf ( element.getElementsByTagName ( "depositBalance" ).item ( 0 ).getTextContent () ) ) ) );
                    }
                    if (deposit.getDepositBalance ().compareTo ( BigDecimal.ZERO ) <= 0) {
                        throw new ArithmeticException ( "your Deposit Balance Is Incorrect" );
                    }
                    deposit.setDurationInDays ( Integer.parseInt ( String.valueOf ( Objects.requireNonNull ( element ).getElementsByTagName ( "durationInDays" ).item ( 0 ).getTextContent () ) ) );
                    if (deposit.getDurationInDays () <= 0) {
                        throw new ArithmeticException ( "Duration Days Is incorrect" );
                    }
                    String depositTypeStr = element.getElementsByTagName ( "depositType" ).item ( 0 ).getTextContent ();
                    DepositType depositType =(DepositType)Class.forName(depositTypeStr).newInstance();
//                    Class typeOfDeposit = Class.forName ( depositTypeStr );
//                    DepositType depositType = (DepositType) typeOfDeposit.newInstance ();
                    deposit.setDepositType ( depositType );

                    BigDecimal bigDecimal = new BigDecimal ( "36500" );
                    BigDecimal interestRate = deposit.getDepositBalance ().multiply
                            ( new BigDecimal ( deposit.getDurationInDays () ).multiply ( new BigDecimal ( depositType.getInterestRate () ) ) ).divide ( bigDecimal , BigDecimal.ROUND_DOWN);
                    deposit.setPayedInterest ( interestRate );
                    deposits.add ( deposit );
                } catch (ArithmeticException | NullPointerException b) {
                    b.printStackTrace ();
                } catch (IllegalAccessException e) {
                    e.printStackTrace ();
                } catch (InstantiationException e) {
                    e.printStackTrace ();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace ();
                }

            }
            Collections.sort ( deposits );
            RandomAccessFile outputFile = new RandomAccessFile ( "sourceoutPut22.txt", "rw" );
            for (Deposit depositSorted : deposits) {
                System.out.println ( depositSorted.getPayedInterest () );
                outputFile.writeBytes ( depositSorted.getCustomerNumber () + "#" + depositSorted.getPayedInterest () + "\n" );
            }
            outputFile.close ();


        } catch (SAXException | ParserConfigurationException | IOException | NullPointerException e) {
            e.printStackTrace ();
        }


    }
}