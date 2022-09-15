package it.polimi.db2.gma.controllers.admin;

import it.polimi.db2.gma.controllers.Utils;
import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.services.ProductService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/admin/CreateProduct")
@MultipartConfig
public class CreateProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService pService;

	public CreateProduct() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = "/WEB-INF/CreateProduct.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		String name = request.getParameter("name");

		Part imgFile = request.getPart("image");
		InputStream imgContent = imgFile.getInputStream();
		byte[] imgByteArray = Utils.readImage(imgContent);

		if (name == null || name.isEmpty() || imgByteArray.length == 0) {
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product parameters");
			Utils.processError(ctx, templateEngine, "Invalid product parameters");
			return;
		}

		Product product = pService.createProduct(name, imgByteArray);

		String path = "/WEB-INF/Admin.html";
		ctx.setVariable("createdProduct", product);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
	}

}
