SELECT *
FROM Salespeople
WHERE reports_to = 1488;

SELECT *
FROM Customers
WHERE SID = 1496;

SELECT DISTINCT *
FROM Sales NATURAL JOIN Customers
WHERE CID = "C2022040001";

SELECT *
FROM Cars 
WHERE VIN = "ML32F4FJ2JHF10325";
#WHERE Salespeople_SID = 1492;
## WHERE Customers_CID = "C2019093153"
## GROUP BY saleID;

SELECT *
FROM Cars
WHERE list_price > 50000.00;

SELECT *
FROM Sales;

SELECT *
FROM Cars;

INSERT INTO Cars (VIN, license_num, manufacturer, model, list_price, date_manufacture, date_delivery, num_doors, weight, capacity, color, milage, tradein_status, standard_warranty)
VALUES ("178BSH38ETSGO937S", "FOW5279", "Toyota", "Camry", 8000, '2011-02-11', '2023-01-02', 4, 3050, 5, "Silver", 100346, "Y", "1 yrs");

#query for customers who bought a car
SELECT * 
FROM Customers NATURAL JOIN Sales
WHERE EXISTS(SELECT CID
FROM Customers
WHERE Customers.CID = Sales.CID);

#query for customers who haven't made a purchase yet
SELECT *
FROM Customers
LEFT JOIN Sales ON Customers.CID = Sales.CID
WHERE Sales.CID IS NULL;

#Bill of sale query for a customer:
SELECT saleID, CID, SID, first_name, last_name, VIN, license_num, milage
FROM Customers NATURAL JOIN Sales NATURAL JOIN Cars
WHERE CID = "C2019093155";

#Query for monthly report salesperson sales
SELECT Salespeople.SID, first_name, last_name, SUM((price_negotiated + fees)* commisison) AS tot_sales
FROM Sales NATURAL JOIN Salespeople
WHERE MONTH(date_sale) = 7 AND YEAR(date_sale) = 2022
GROUP BY SID;	
