package com.giftcard.emails.giftcardemailtemplates.congig;

import com.github.jknack.handlebars.Handlebars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@Configuration
public class HandlebarsConfig {


@Bean
public Handlebars handlebars() {
Handlebars handlebars = new Handlebars();


// date format helper
handlebars.registerHelper("formatDate", (context, options) -> {
if (context == null) return "";
try {
String s = String.valueOf(context);
LocalDate d = LocalDate.parse(s);
return d.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
} catch (Exception ex) {
return String.valueOf(context);
}
});


// simple currency helper
handlebars.registerHelper("currency", (context, options) -> {
if (context == null) return "₹0.00";
try {
BigDecimal bd = new BigDecimal(String.valueOf(context));
NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
return nf.format(bd);
} catch (Exception ex) {
return String.valueOf(context);
}
});


return handlebars;
}
}