package com.enipro.presentation.payments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.enipro.R;
import com.enipro.data.remote.model.Feed;
import com.enipro.model.Constants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

/**
 * Payment for items on Enipro depends on the paystack platform.
 * Changes to the API used for payments will require changing verification of credit card and payment process
 * which is dependent on the paystack API.
 * <p>
 * NB: Due to considerations in scalability and changes in libraries and API, only the payment process
 * depends on paystack. All other process are not affected by paystack API.
 */
public class PaymentsFormActivity extends AppCompatActivity {

    private String email, card_number, cvvStr;
    private int expiryMonth, expiryYear;
    private Feed paymentFeed; // The feed whose content is to be paid for.

    @BindView(R.id.card_number)
    EditText cardNumber;
    @BindView(R.id.expiry_date)
    EditText expiryDate;
    @BindView(R.id.cvv)
    EditText cvv;

    @BindView(R.id.pay)
    Button payAmount;
    @BindView(R.id.close)
    ImageButton close;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, PaymentsFormActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_form);
        ButterKnife.bind(this);

        // Get calling intent data
        paymentFeed = Parcels.unwrap(getIntent().getParcelableExtra(Constants.FEED_EXTRA));

        initUIControls(); // Initialise UI Controls to respond accordingly.
    }


    /**
     * Initialises UI Controls (edit text and buttons on the payments form).
     */
    void initUIControls() {

        // Attach listeners to all edit text fields to perform appropriate operations when triggered.


        initCardNumberTextWatcher();

        // Set focus change listeners for the edit text.
        cardNumber.setOnFocusChangeListener(editTextFocusListener);
        expiryDate.setOnFocusChangeListener(editTextFocusListener);
        cvv.setOnFocusChangeListener(editTextFocusListener);

        // Text change listener for expiry date.
        expiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                // Save the month data
                if (charSequence.length() == 2) {
                    // Grab the month data and save it then insert a "/"
                    expiryMonth = Integer.valueOf(charSequence.toString());
                }

                // When the third digit is to be go in, the '/' character will precede the third character
                if (charSequence.length() == 3) {
                    // Get first two characters
                    String firsttwo = charSequence.toString().substring(0, 2);
                    String third = charSequence.toString().substring(2);

                    // Check the third character to identify deletion from insertion of digits
                    if (!third.equals("/")) {
                        charSequence = firsttwo + "/" + third;
                        expiryDate.setText(charSequence);
                        expiryDate.setSelection(charSequence.length()); // This is to advance the cursor after the modification is made.
                    }
                }

                // End of value has been reached, grab the year data and save it.
                if (charSequence.length() == 5) {
                    String fullDate = charSequence.toString();
                    expiryYear = Integer.valueOf(fullDate.substring(fullDate.indexOf('/') + 1));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        close.setOnClickListener(view -> finish()); // Close the activity and return to calling activity when close button is clicked.
        /* Handles payment activation process and verification of credit card details. */
        initPayAction();

    }

    /**
     * This programmatically changes the image to the card type image and set the maximum
     * number of lines for depending on the card type. The default length for the field is 19 which
     * is the maximum length of a credit card number.
     */
    void initCardNumberTextWatcher() {
        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // After every fourth character, insert a huge space.
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private View.OnFocusChangeListener editTextFocusListener = ((view, hasFocus) -> {
        if (hasFocus)
            view.setBackgroundResource(R.drawable.active_round_edittext);
        else
            view.setBackgroundResource(R.drawable.nonactive_round_edittext);
    });


    /**
     * Handles operation when pay button is clicked. This basically just verifies the credit card information
     * and starts the payment process.
     */
    void initPayAction() {
        // Get account

        payAmount.setOnClickListener(view -> {

            // Check to see if all inputs are correct and filled.


            // Initialise payment process
            chargeCard();

        });
    }

    private void chargeCard() {

        // Validate card information and initiate payment transaction
        Card card = new Card(card_number, expiryMonth, expiryYear, cvvStr);
        if (!card.isValid()) {
            // TODO Show error messages for the card and return
            return;
        }
        Charge charge = new Charge();
        charge.setCard(card);

        // TODO Get the amount to charge for from the post activity calling this activity.
        int payment_amount = paymentFeed.getPremiumDetails().getPayment_amount() / 1000;
        charge.setAmount(payment_amount);

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
                String paymentRef = transaction.getReference();
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                // This is called when an error was discovered.
            }
        });

    }
}
