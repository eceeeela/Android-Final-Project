package com.example.real_timedbaccount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {
    private ArrayList<Book> booksList;
    private ArrayList<Book> cartList;
    private ArrayList<Book> returnBookList;
    private ArrayList<Book> bookshelfList;
    private Context context;

    public BookAdapter(Context context, ArrayList<Book> booksList, ArrayList<Book> cartList,
                       ArrayList<Book> returnBookList, ArrayList<Book> bookshelfList) {
        super(context, 0, booksList);
        this.booksList = booksList;
        this.cartList = cartList;
        this.returnBookList = returnBookList;
        this.bookshelfList = bookshelfList;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        }

        Book book = booksList.get(position);

        // UI Elements
        TextView titleTextView = convertView.findViewById(R.id.book_title);
        ImageView bookImageView = convertView.findViewById(R.id.bookImage);
        Button addToCartButton = convertView.findViewById(R.id.btn_addToCart);

        // Set Book Title
        titleTextView.setText(book.getTitle());

        // Load Book Image
        if (book.getImageURL() != null) {
            Picasso.get().load(book.getImageURL()).into(bookImageView);
        } else {
            bookImageView.setImageResource(R.drawable.book_image);
        }

        // Add to Cart Logic
        addToCartButton.setOnClickListener(v -> {
            if (!cartList.contains(book)) {
                cartList.add(book);
                book.setRented(true); // Mark book as rented

                // Add to returnBookList and bookshelfList
                if (!returnBookList.contains(book)) {
                    returnBookList.add(book);
                }
                if (!bookshelfList.contains(book)) {
                    bookshelfList.add(book);
                }

                Toast.makeText(context, "Book rented successfully", Toast.LENGTH_SHORT).show();

                // Update the adapter to notify the change
                notifyDataSetChanged();

                // Optional: Save to Firebase
                saveBookToFirebase(book);
            } else {
                Toast.makeText(context, "Book already in the cart!", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    // Save to Firebase Method
    private void saveBookToFirebase(Book book) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rentedBooksRef = database.getReference("rentedBooks");

        rentedBooksRef.child(book.getIsbn()).setValue(book)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Book saved to database", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to save book to database", Toast.LENGTH_SHORT).show();
                });
    }
}
