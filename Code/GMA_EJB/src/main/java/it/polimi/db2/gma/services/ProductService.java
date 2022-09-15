package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.exceptions.ProductException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Stateless
public class ProductService {
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;
	@EJB(name = "it.polimi.db2.gma.services/MySqlTxUtils")
	private MySqlTxUtils util;

	public ProductService() {
	}

	public Product findProductId(int productId) {
		Product product = em.find(Product.class, productId);
		return product;
	}

	public List<Product> getAllProducts() throws ProductException {
		List<Product> pList = null;
		try {
			pList = em.createNamedQuery("Product.getAllProducts", Product.class)
					.getResultList();
		} catch (PersistenceException e) {
			throw new ProductException("Could not load products");
		}

		return pList;
	}

	public Product createProduct(String name, byte[] img) {
		Product p = new Product();
		p.setName(name);
		p.setImage(img);
		em.persist(p);
		return p;
	}

}
