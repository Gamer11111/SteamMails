
package archimail;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Archimail {
static int count=0;
    static ArrayList<String> items = new <String>ArrayList();
public static void fetch(String pop3Host, String storeType, String user,
      String password) {
      try {
         // create properties field
         Properties properties = new Properties();
         properties.put("mail.store.protocol", "pop3");
         properties.put("mail.pop3.host", pop3Host);
         properties.put("mail.pop3.port", "995");
         properties.put("mail.pop3.starttls.enable", "true");
         Session emailSession = Session.getDefaultInstance(properties);
         // emailSession.setDebug(true);

         // create the POP3 store object and connect with the pop server
         Store store = emailSession.getStore("pop3s");

         store.connect(pop3Host, user, password);

         // create the folder object and open it
         Folder emailFolder = store.getFolder("INBOX");
         emailFolder.open(Folder.READ_ONLY);



SearchTerm sender = new FromTerm(new InternetAddress("noreply@steampowered.com"));
         // retrieve the messages from the folder in an array and print it
         Message[] messages = emailFolder.search(sender);
         

  
           
         for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            if (message.getSubject().contains("You have sold an item on the Community Market")){
                count++;
           
            writePart(message);
             System.out.println(items);
            }
            }
          System.out.println(count);
         
       // close the store and folder objects
         emailFolder.close(false);
         store.close();

      } catch (NoSuchProviderException e) {
         e.printStackTrace();
      } catch (MessagingException e) {
         e.printStackTrace();
      } catch (RuntimeException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

    public static void main(String[] args) {
    
   
        
      
      String host = "pop.gmail.com";// change accordingly
      String mailStoreType = "pop3";
          try{
        BufferedReader reader =
                   new BufferedReader(new InputStreamReader(System.in));
        
      
              System.out.println("Enter username");
      String username = reader.readLine();
         ;// change accordingly
              System.out.println("Enter password");
      String password =reader.readLine(); // change accordingly
              
      //Call method fetch
      fetch(host, mailStoreType, username, password);
          }
               catch(Exception e)
        {
        e.printStackTrace();
        }
        System.out.println("Press any key to exit");
       Scanner scanner = new Scanner(System.in); 
scanner.nextLine();
   }
    
     public static void writePart(Part p) throws Exception {
          
      if (p instanceof Message)
         //Call methos writeEnvelope
         writeEnvelope((Message) p);

      System.out.println("----------------------------");
     System.out.println("CONTENT-TYPE: " + p.getContentType());

      //check if the content is plain text
      if (p.isMimeType("text/plain")) {
         System.out.println("This is plain text");
         System.out.println("---------------------------");
          System.out.println((String)p.getContent());
      }
      //check if the content has attachment
      else if (p.isMimeType("multipart/*")) {
        System.out.println("This is a Multipart");
         System.out.println("---------------------------");
         Multipart mp = (Multipart) p.getContent();
         int count = mp.getCount();
         for (int i = 0; i < count; i++)
            writePart(mp.getBodyPart(i));
      } 
      //check if the content is a nested message
      else if (p.isMimeType("message/rfc822")) {
        System.out.println("This is a Nested Message");
         System.out.println("---------------------------");
         writePart((Part) p.getContent());
      } 
      //check if the content is an inline image
      else if (p.isMimeType("image/jpeg")) {
         System.out.println("--------> image/jpeg");
         Object o = p.getContent();
         InputStream x = (InputStream) o;
         // Construct the required byte array
         System.out.println("x.length = " + x.available());
          int i = 0;
         byte[] bArray = new byte[x.available()];
         while ((i = (int) ((InputStream) x).available()) > 0) {
            int result = (int) (((InputStream) x).read(bArray));
            if (result == -1)
        

            break;
         }
         FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
         f2.write(bArray);
      } 
      else if (p.getContentType().contains("image/")) {
        System.out.println("content type" + p.getContentType());
         File f = new File("image" + new Date().getTime() + ".jpg");
         DataOutputStream output = new DataOutputStream(
            new BufferedOutputStream(new FileOutputStream(f)));
            com.sun.mail.util.BASE64DecoderStream test = 
                 (com.sun.mail.util.BASE64DecoderStream) p
                  .getContent();
         byte[] buffer = new byte[1024];
         int bytesRead;
         while ((bytesRead = test.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
         }
      } 
      else {
        
         Object o = p.getContent();
         if (o instanceof String) {
            System.out.println("This is a string");
            System.out.println("---------------------------");
            
             //Extract html page and retrieve item table
             Document doc =Jsoup.parse((String)p.getContent());
              Element table = doc.select("table").get(2);
                Elements rows = table.select("tr");
                
    Element row = rows.get(0);
    Elements cols = row.select("td");

             items.add(cols.get(0).text());
        }
     
         else if (o instanceof InputStream) {
            System.out.println("This is just an input stream");
            System.out.println("---------------------------");
            InputStream is = (InputStream) o;
            is = (InputStream) o;
            int c;
            while ((c = is.read()) != -1)
               System.out.write(c);
         } 
         else {
            System.out.println("This is an unknown type");
            System.out.println("---------------------------");
            System.out.println(o.toString());*/
         }
      }

   }
   /*
   * This method would print FROM,TO and SUBJECT of the message
   */
   public static void writeEnvelope(Message m) throws Exception {
     System.out.println("This is the message envelope");
      System.out.println("---------------------------");
      Address[] a;
      
      

     // FROM
      if ((a = m.getFrom()) != null) {
         for (int j = 0; j < a.length; j++)
         System.out.println("FROM: " + a[j].toString());
      }

      // TO
      if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
         for (int j = 0; j < a.length; j++)
         System.out.println("TO: " + a[j].toString());
      }
    //SUBJECT
      if (m.getSubject() != null)
          subject = m.getSubject();

   }
}


   
   
 

