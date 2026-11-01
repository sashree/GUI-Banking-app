package com.nexabank.ui;

import com.nexabank.exception.BankingException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.service.AuthService;
import com.nexabank.service.BankingService;
import com.nexabank.ui.components.AppDialogs;
import com.nexabank.ui.components.GradientPanel;
import com.nexabank.ui.components.RoundedButton;
import com.nexabank.ui.components.RoundedField;
import com.nexabank.ui.theme.AppTheme;
import com.nexabank.ui.theme.IconFactory;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {

    private final AuthService auth;
    private final BankingService banking;
    private final CardLayout forms = new CardLayout();
    private final JPanel formHost = new JPanel(forms);

    private final JTextField accountField = RoundedField.lightText();
    private final JPasswordField pinField = RoundedField.lightPassword();
    private final JTextField nameField = RoundedField.lightText();
    private final JTextField emailField = RoundedField.lightText();
    private final JTextField phoneField = RoundedField.lightText();
    private final JPasswordField newPinField = RoundedField.lightPassword();
    private final JPasswordField confirmPinField = RoundedField.lightPassword();

    public LoginFrame(AuthService auth, BankingService banking) {
        super("NexaBank — Secure Sign In");
        this.auth = auth;
        this.banking = banking;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 720));
        setSize(1140, 780);
        setLocationRelativeTo(null);
        setContentPane(buildRoot());
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildBrand(), BorderLayout.WEST);
        root.add(buildForms(), BorderLayout.CENTER);
        return root;
    }

    private JPanel buildBrand() {
        GradientPanel brand = new GradientPanel(new BorderLayout());
        brand.setPreferredSize(new Dimension(480, 720));
        brand.setBorder(BorderFactory.createEmptyBorder(48, 48, 48, 48));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("NexaBank", IconFactory.logo(42), SwingConstants.LEFT);
        logo.setIconTextGap(14);
        logo.setFont(AppTheme.fontBold(28));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel kicker = new JLabel("ACADEMIC BANKING SIMULATOR");
        kicker.setFont(AppTheme.font(12));
        kicker.setForeground(AppTheme.CYAN);
        kicker.setBorder(BorderFactory.createEmptyBorder(18, 0, 10, 0));
        kicker.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel headline = new JLabel("<html>Modern banking,<br>built to teach Java OOP.</html>");
        headline.setFont(AppTheme.fontBold(32));
        headline.setForeground(Color.WHITE);
        headline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel copy = new JLabel("<html><div style='width:320px'>Deposit, withdraw, transfer, and review a full ledger — all persisted locally with salted PIN hashing and BigDecimal money math.</div></html>");
        copy.setFont(AppTheme.font(15));
        copy.setForeground(new Color(203, 213, 225));
        copy.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        copy.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(logo);
        top.add(kicker);
        top.add(headline);
        top.add(copy);

        JPanel bullets = new JPanel();
        bullets.setOpaque(false);
        bullets.setLayout(new BoxLayout(bullets, BoxLayout.Y_AXIS));
        bullets.add(bullet("Encrypted PIN storage (PBKDF2)"));
        bullets.add(bullet("SQLite ledger that survives restarts"));
        bullets.add(bullet("Demo accounts ready for viva walkthroughs"));

        JLabel disclaimer = new JLabel("<html><div style='width:320px'>Educational simulator only. Not a real bank and not suitable for real money.</div></html>");
        disclaimer.setFont(AppTheme.font(12));
        disclaimer.setForeground(AppTheme.MUTED);

        brand.add(top, BorderLayout.NORTH);
        brand.add(bullets, BorderLayout.CENTER);
        brand.add(disclaimer, BorderLayout.SOUTH);
        return brand;
    }

    private JLabel bullet(String text) {
        JLabel label = new JLabel("  ●  " + text);
        label.setFont(AppTheme.font(14));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel buildForms() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(AppTheme.LOGIN_PANEL);
        formHost.setOpaque(false);
        formHost.add(loginCard(), "login");
        formHost.add(registerCard(), "register");
        wrap.add(formHost);
        return wrap;
    }

    private JPanel loginCard() {
        JPanel card = whiteCard();
        card.add(title("Welcome back"));
        card.add(subtitle("Sign in with your 10-digit account number and 4-digit PIN."));
        card.add(Box.createVerticalStrut(18));
        card.add(fieldLabel("Account number"));
        card.add(accountField);
        card.add(Box.createVerticalStrut(12));
        card.add(fieldLabel("PIN"));
        card.add(pinField);
        card.add(Box.createVerticalStrut(18));

        RoundedButton signIn = RoundedButton.primary("Sign in");
        signIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        signIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        signIn.addActionListener(e -> doLogin());
        pinField.addActionListener(e -> doLogin());
        card.add(signIn);

        card.add(Box.createVerticalStrut(16));
        card.add(link("New here? Open a demo account", () -> forms.show(formHost, "register")));
        card.add(Box.createVerticalStrut(18));
        card.add(demoHint());
        return card;
    }

    private JPanel registerCard() {
        JPanel card = whiteCard();
        card.add(title("Open a demo account"));
        card.add(subtitle("Creates a local savings account with a zero opening balance."));
        card.add(Box.createVerticalStrut(14));
        card.add(fieldLabel("Full name"));
        card.add(nameField);
        card.add(Box.createVerticalStrut(8));
        card.add(fieldLabel("Email"));
        card.add(emailField);
        card.add(Box.createVerticalStrut(8));
        card.add(fieldLabel("Phone (10 digits)"));
        card.add(phoneField);
        card.add(Box.createVerticalStrut(8));
        card.add(fieldLabel("Choose a 4-digit PIN"));
        card.add(newPinField);
        card.add(Box.createVerticalStrut(8));
        card.add(fieldLabel("Confirm PIN"));
        card.add(confirmPinField);
        card.add(Box.createVerticalStrut(16));

        RoundedButton create = RoundedButton.primary("Create account");
        create.setAlignmentX(Component.LEFT_ALIGNMENT);
        create.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        create.addActionListener(e -> doRegister());
        card.add(create);
        card.add(Box.createVerticalStrut(12));
        card.add(link("Already have an account? Sign in", () -> forms.show(formHost, "login")));
        return card;
    }

    private JPanel whiteCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(32, 32, 32, 32)
        ));
        card.setPreferredSize(new Dimension(440, 640));
        return card;
    }

    private JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(26));
        label.setForeground(new Color(15, 23, 42));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel subtitle(String text) {
        JLabel label = new JLabel("<html><div style='width:340px'>" + text + "</div></html>");
        label.setFont(AppTheme.font(13));
        label.setForeground(new Color(100, 116, 139));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        return label;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(12));
        label.setForeground(new Color(51, 65, 85));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }

    private JLabel link(String text, Runnable action) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.font(13));
        label.setForeground(AppTheme.ACCENT);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });
        return label;
    }

    private JPanel demoHint() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(239, 246, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel("<html><b>Demo accounts</b><br>1001001001 / 1234 · Priya Sharma<br>1001001002 / 5678 · Arjun Mehta<br>1001001003 / 9012 · Ananya Reddy</html>");
        label.setFont(AppTheme.font(12));
        label.setForeground(new Color(30, 64, 175));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        return panel;
    }

    private void doLogin() {
        try {
            SavingsAccount account = auth.login(accountField.getText(), pinField.getPassword());
            pinField.setText("");
            openDashboard(account);
        } catch (BankingException ex) {
            AppDialogs.error(this, "Sign in failed", ex.getMessage());
        }
    }

    private void doRegister() {
        try {
            if (!String.valueOf(newPinField.getPassword()).equals(String.valueOf(confirmPinField.getPassword()))) {
                AppDialogs.error(this, "PIN mismatch", "The two PIN entries do not match.");
                return;
            }
            SavingsAccount account = auth.register(
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    newPinField.getPassword()
            );
            confirmPinField.setText("");
            AppDialogs.info(this, "Account created",
                    "Welcome " + account.getHolderName() + ".\nYour account number is " + account.getAccountNumber()
                            + ".\nPlease remember your PIN. Opening balance is ₹0.00.");
            openDashboard(account);
        } catch (BankingException ex) {
            AppDialogs.error(this, "Could not create account", ex.getMessage());
        }
    }

    private void openDashboard(SavingsAccount account) {
        MainFrame main = new MainFrame(auth, banking, account, this);
        main.setVisible(true);
        setVisible(false);
    }

    public void prepareForReturn() {
        accountField.setText("");
        pinField.setText("");
        forms.show(formHost, "login");
        setVisible(true);
        toFront();
    }
}
