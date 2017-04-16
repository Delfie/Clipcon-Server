package sprout.clipcon.server.controller;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import sprout.clipcon.server.model.Group;

/* maxFileSize: �ִ� ���� ũ��(100MB)
 * fileSizeThreshold: 1MB ������ ������ �޸𸮿��� �ٷ� ���
 * maxRequestSize:  */
@MultipartConfig(location = "C:", maxFileSize = 1024 * 1024 * 100, fileSizeThreshold = 1024 * 1024, maxRequestSize = 1024 * 1024 * 100)
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {

	// // ���ε� ������ ������ ��ġ
	// private final String UPLOAD_DIRECTORY =
	// "C:\\Users\\Administrator\\Desktop\\primaryRoomKey";
	private Server server = Server.getInstance();
	private String userEmail = null;
	private String groupPK = null;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doGet()");
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ��û���� http request ���� ���
		requestMsgLog(request);
		userEmail = request.getParameter("userEmail");
		groupPK = request.getParameter("groupPK");
		System.out.println("userEmail: " + userEmail + ", groupPK: " + groupPK);

		Part filePart = null;

		// ���� file���� ������
		for (Part part : request.getParts()) {
			/*
			 * To find out file name, parse header value of content-disposition e.g. form-data; name="file"; filename=""
			 */
			System.out.println("<headerName: headerValue>");
			for (String headerName : part.getHeaderNames()) {
				System.out.println(headerName + ": " + part.getHeader(headerName));
			}

			// Get a normal parameter
			if (part.getName().equals("stringData")) {
				System.out.println("....IN......(stringData)");

				String paramValue = getStringFromStream(part.getInputStream());
				System.out.println("paramValue: " + paramValue);
			}

			if (part.getName().equals("imageData")) {
				System.out.println("....IN.......(imageData)");

				Image imageData = getImageDataStream(part.getInputStream());
				System.out.println(imageData.toString());
			}

			if (part.getName().equals("multipartFileData")) {
				System.out.println("....IN.......(multipartFileData)");

				String fileName = getFilenameInHeader(part.getHeader("content-disposition"));
				System.out.println("fileName: " + fileName);

				filePart = part; // Absolute path doesn't work.
				filePart.write(fileName);
			}
			System.out.println();
		}
	}

	/** Request Header "content-disposition"���� filename ���� */
	public String getFilenameInHeader(String requestHeader) {
		int beginIndex = requestHeader.indexOf("filename") + 10;
		int endIndex = requestHeader.length() - 1;

		String fileName = requestHeader.substring(beginIndex, endIndex);

		return fileName;
	}

	/** String Data�� �����ϴ� Stream */
	public String getStringFromStream(InputStream stream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;

		try {
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return stringBuilder.toString();
	}

	/** Image Data�� �����ϴ� Stream */
	public Image getImageDataStream(InputStream stream) throws IOException {
		byte[] imageInByte;

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = stream.read(buffer)) != -1;)
				byteArrayOutputStream.write(buffer, 0, len);

			byteArrayOutputStream.flush();

			imageInByte = byteArrayOutputStream.toByteArray();
		}

		// convert byte array back to BufferedImage
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageInByte);
		BufferedImage bImageFromConvert = ImageIO.read(byteArrayInputStream);

		Image ImageData = (Image) bImageFromConvert;

		return ImageData;
	}

	/** request msg ��� */
	public void requestMsgLog(HttpServletRequest request) {

		/* server�� ���� request ������ ���� */
		System.out.println("==================������==================");
		System.out.println("Request Method: " + request.getMethod());
		System.out.println("Request RequestURI: " + request.getRequestURI());
		System.out.println("Request Protocol: " + request.getProtocol());

		/* server�� ���� request ��� ���� */
		/* server�� ���� �⺻���� request header msg ���� */
		System.out.println("==================���====================");
		Enumeration headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();

			System.out.println(headerName + ": " + request.getHeader(headerName));
		}
		System.out.println("-------------------------------------------");
		System.out.println("Request LocalAddr: " + request.getLocalAddr());
		System.out.println("Request LocalName: " + request.getLocalName());
		System.out.println("Request LocalPort: " + request.getLocalPort());
		System.out.println("-------------------------------------------");
		System.out.println("Request RemoteAddr: " + request.getRemoteAddr());
		System.out.println("Request RemoteHost: " + request.getRemoteHost());
		System.out.println("Request RemotePort: " + request.getRemotePort());
		System.out.println("Request RemoteUser: " + request.getRemoteUser());

		System.out.println("================����Ƽ ����================");
		System.out.println("__sequence: " + request.getParameter("__sequence"));
		System.out.println("uploadFilename: " + request.getParameter("uploadFilename"));
		System.out.println("Content-Disposition: " + request.getParameter("Content-Disposition"));
		System.out.println("===========================================");
		System.out.println();
		System.out.println();
	}
	/**@author delf*/
	public void Example() {
		Group targetGroup = server.getGroupByPrimaryKey("�׷�Ű");
		
	}
}