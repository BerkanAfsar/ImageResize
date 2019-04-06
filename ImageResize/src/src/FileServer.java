package src;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTMLEditorKit.Parser;
import javax.xml.ws.Response;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

public class FileServer extends AbstractHandler {

	
	public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response) 
            throws IOException, ServletException
	{
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		
		response.setContentType("image/jpg");
		//FileInputStream bis = new FileInputStream("C:\\Users\\Seher\\Desktop\\small_set"+target);
		//BufferedImage myImg = ImageIO.read(bis);
		URL url = new URL("http://bihap.com/img/");
		BufferedImage myImg = ImageIO.read(url);
		
		ArrayList<Integer> values = new ArrayList<Integer>();
		Enumeration<String> parameterNames= request.getParameterNames(); //? sonrasý parametreyi verir
		
		 while(parameterNames.hasMoreElements()) {
			String name= (String) parameterNames.nextElement();
			
			if(name.equals("width"))
			{
				values.add(0, Integer.parseInt(request.getParameter(name).toString()));
				
			}else if(name.equals("height"))
				values.add(1, Integer.parseInt(request.getParameter(name).toString()));
			else if(name.equals("color")&&((request.getParameter(name).toString()).equals("gray")))
			{
				ImageIO.write(grayImg(myImg), "jpg", response.getOutputStream());
			}
			
		}
		 
		 if(values.size()==2)
		 {
			 String str=scale("C:\\Users\\Seher\\Desktop\\small_set"+target,values.get(0),values.get(1));
			 myImg=ImageIO.read(new File(str));
		 }
		 ImageIO.write(myImg, "jpg", response.getOutputStream()); 

		/*try
		{
			sendFile(bis, response.getOutputStream());
			
		}
		catch(Exception ex)
		{
			Logger.getLogger(HelloWorld.class.getName()).log(Level.SEVERE, null, ex);
		}	
		baseRequest.setHandled(true);*/
		
	}
		
		
	
	/*public static void sendFile(FileInputStream bis, ServletOutputStream outputStream) throws Exception {//Servlet
		byte[] buffer=new byte[1024];
		int bytesRead;
		
	
		while((bytesRead=bis.read(buffer))!=-1)
		{
			outputStream.write(buffer,0,bytesRead);
		}
				
		bis.close();
		outputStream.close();
	}*/
		
	public String scale(String path,int width,int height) throws IOException
	{
		//File file = new File("resim.jpeg");
		BufferedImage orgImage = ImageIO.read(new File(path));
		BufferedImage resizedCopy=createResizedCopy(orgImage,width,height, true);
		
		File tosave=new File(path);
		ImageIO.write(resizedCopy, "jpg",tosave);//tosave YERÝNE resp.getOutputStream() olabilir mi?
		return tosave.getAbsolutePath();
	}
	
	public BufferedImage createResizedCopy(Image orgCopy, int scaledWidth, int scaledHeight, boolean preserveAlpha)
	{
		System.out.println("resizing..");
		int imageType=preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI=new BufferedImage(scaledWidth,scaledHeight,imageType);
		Graphics2D g=scaledBI.createGraphics();
		
		if(preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		
		g.drawImage(orgCopy, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();
		return scaledBI;
	}
	
	public BufferedImage grayImg(BufferedImage orgImage) throws IOException
	{
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		BufferedImage grayImage = op.filter(orgImage, null);
		return grayImage;	
	}

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		
		ContextHandler context=new ContextHandler();
		context.setContextPath("/path1"); //hangi handlera yonlenmemiz gerektiðini gösterir
		context.setHandler(new FileServer()); //handler
    
		server.setHandler(context);
        server.start();
        server.join();

	}
}

