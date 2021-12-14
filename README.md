# Libreria-Spring-Boot

Web administrator of a Library where CRUD (Create, Read, Update & Delete) functions can be managed and performed in Book Entities linked to Author and Editorial Entities, equally editable and customizable. <br>
List of actions that can be performed with Book Entities:

<ul>
<li> Create Books with all its main characteristics without repeating the Title and ISBN </li>
<li> Modify attributes of Books already created </li>
<li> Choose Authors from a list of Authors created AND enabled </li>
<li> Choose Editorials from a list of Editorials created AND enabled </li>
<li> See the list of all Books, Authors and Editorials with all their attributes and be able to disable them in one click </li>
<li> Establish a quantity of available copies of each Book </li>
</ul>
<br>
Furthermore, it is also possible to register and have a list of Clients Entities with their personal data (Name, Surname, ID and Telephone number) and to be able to make loans in favor of a Client
about a particular Book. <br>
This loan functionality has the following features:
<ul>
<li> Make Loans only in enabled Clients and with Books also enabled </li>
<li> Make as many Loans as necessary as long as the Book has copies available, otherwise an error message will be shown </li>
<li> Have a list with all the Loans (both pending and those already returned) with the loan date and the repayment date (if any) </li>
<li> Cancel a Loan (make the refund) in a single click </li>
<li> Old Loans can also be reactivated if and only if there are still remaining copies of such Book  </li>
</ul>

<br> <hr>
This is a project made as an exercise in the <strong> Java 8 </strong> language using the <strong> Spring Framework with Spring Boot </strong> to design a
application with a <strong> MVC layout model. </strong> <br> <br>
In it, you will be able to Create, Read, Modify, and Delete <strong> (CRUD) </strong> essential data of Entities such as Books, Authors, Editorials, Clients and Loans. <br>
In addition, you can make real loans of your Books to your Clients (as long as there are stocks). <br>
You will have a record of the date on which each Loan was made, which ones are pending repayment and which ones have already been returned and on what date.
<br> <br>
This exercise is part of the <strong><a href="https://carreras.eggeducacion.com/ar/programacion/" target="_blank">FullStack Developer course from the EGG Cooperation Institute.</a></strong>

<hr>
<h2>Maven dependencies applied: </h2>
<ul>
  <li>Spring Boot DevTools</li>
  <li>Spring Web</li>
  <li>Thymeleaf</li>
  <li>Spring Security</li>
  <li>Spring Data JPA</li>
  <li>MySQL Driver</li>
</ul>

<h2>Other technologies</h2>
<ul>
  <li>HTML</li>
  <li>CSS</li>
  <li>Bootstrap Components</li>
  <li>Bootstrap Free Themes</li>
  <li>MySQL Workbench</li>
  <li>NetBeans IDE</li>
  <li>Visual Studio Code</li>
</ul>
