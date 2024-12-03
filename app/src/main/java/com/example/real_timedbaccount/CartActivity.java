package com.example.real_timedbaccount;


import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Retrieve the list of books from the intent
        ArrayList<Book> cartList = getIntent().getParcelableArrayListExtra("cartList");

        // Display book details
        TextView cartDetails = findViewById(R.id.cart_details);
        if (cartList != null) {
            StringBuilder details = new StringBuilder();
            for (Book book : cartList) {
                details.append("Title: ").append(book.getTitle()).append("\n")
                        .append("Author: ").append(book.getAuthor()).append("\n")
                        .append("ISBN: ").append(book.getIsbn()).append("\n")
                        .append("Due Date: ").append(book.getDueDate()).append("\n\n");
            }
            cartDetails.setText(details.toString());
        }

        // Rent All button
        Button rentAllButton = findViewById(R.id.btn_rentAll);
        rentAllButton.setOnClickListener(v -> {
            // Handle rent all action
            if (cartList != null) {
                cartList.clear(); // Clear cart after renting
                cartDetails.setText("No books in cart.");
            }
        });
    }
}
