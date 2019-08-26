package com.telusko.DemoHib;



import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;

public class App {
public static void main(String[] args)
{
Alian alian=new Alian();
alian.setId(13);
alian.setName("sweety");
alian.setGender("fimale");
alian.setGrade("A");
alian.setSemester("five");
alian.setTerm("three");
//alian.setRank("10");
Configuration con=new Configuration().configure().addAnnotatedClass(Alian.class);
ServiceRegistry rg=new ServiceRegistryBuilder().applySettings(con.getProperties()).buildServiceRegistry();
SessionFactory sf=con.buildSessionFactory(rg);
Session session=sf.openSession();
Transaction tx=session.beginTransaction();
//save data in database
session.save(alian);

tx.commit();


/*featch data from database
alian=(Alian)session.get(Alian.class,4);
System.out.print(alian);
*/

//retrieve all data from table

/*Query q=session.createQuery("from Alian");
List<Alian>al=q.list();
for(Alian a:al)
{
  System.out.println(a);	
}
*/
}
}
