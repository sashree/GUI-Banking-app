package com.nexabank.ui;

import com.nexabank.exception.BankingException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.service.AuthService;
import com.nexabank.service.BankingService;
import com.nexabank.ui.components.AppDialogs;
import com.nexabank.ui.components.NavButton;
import com.nexabank.ui.components.RoundedButton;
import com.nexabank.ui.panels.DashboardPanel;
import com.nexabank.ui.panels.DepositPanel;
import com.nexabank.ui.panels.HistoryPanel;
import com.nexabank.ui.panels.ProfilePanel;
import com.nexabank.ui.panels.TransferPanel;
import com.nexabank.ui.panels.WithdrawPanel;
import com.nexabank.ui.theme.AppTheme;
import com.nexabank.ui.theme.IconFactory;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    public interface Refreshable {
        void refresh();
    }

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  •  hh:mm:ss a");

    private final AuthService auth;
    private final BankingService banking;
    private final LoginFrame loginFrame;
    private SavingsAccount account;

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel nameLabel = new JLabel();
    private final JLabel accountLabel = new JLabel();
    private final JLabel clockLabel = new JLabel();
    private final Map<String, NavButton> nav = new LinkedHashMap<>();
    private final Map<String, Refreshable> pages = new LinkedHashMap<>();

    public MainFrame(AuthService auth, BankingService banking, SavingsAccount account, LoginFrame loginFrame) {
        super("NexaBank");
        this.auth = auth;
        this.banking = banking;
        this.account = account;
        this.loginFrame = loginFrame;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 740));
        setSize(1280, 820);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.NAVY);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(), BorderLayout.CENTER);
        setContentPane(root);

        showPage("dashboard");
        Timer timer = new Timer(1000, e -> clockLabel.setText(CLOCK.format(LocalDateTime.now())));
        timer.start();
        clockLabel.setText(CLOCK.format(LocalDateTime.now()));
    }

    public SavingsAccount currentAccount() {
        return account;
    }

    public AuthService auth() {
        return auth;
    }

    public BankingService banking() {
        return banking;
    }

    public void reloadAccount() {
        try {
            account = banking.getAccount(account.getAccountNumber());
            nameLabel.setText(account.getHolderName());
            accountLabel.setText("A/C  " + account.getAccountNumber());
            pages.values().forEach(Refreshable::refresh);
        } catch (BankingException e) {
            AppDialogs.error(this, "Refresh failed", e.getMessage());
        }
    }

    public void showPage(String key) {
        cards.show(content, key);
        nav.forEach((id, button) -> button.setActive(id.equals(key)));
        Refreshable page = pages.get(key);
        if (page != null) {
            page.refresh();
        }
    }

    public void logout() {
        if (!AppDialogs.confirm(this, "Sign out", "Sign out of NexaBank?")) {
            return;
        }
        dispose();
        loginFrame.prepareForReturn();
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(AppTheme.SIDEBAR);
        side.setPreferredSize(new Dimension(248, 800));
        side.setLayout(new BorderLayout());
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.LINE));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(BorderFactory.createEmptyBorder(24, 20, 16, 20));
        JLabel logo = new JLabel("NexaBank", IconFactory.logo(32), SwingConstants.LEFT);
        logo.setIconTextGap(10);
        logo.setFont(AppTheme.fontBold(20));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tag = new JLabel("Personal Banking");
        tag.setFont(AppTheme.font(12));
        tag.setForeground(AppTheme.MUTED);
        tag.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.add(logo);
        brand.add(Box.createVerticalStrut(4));
        brand.add(tag);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        menu.add(navItem("dashboard", "Dashboard", "dashboard"));
        menu.add(Box.createVerticalStrut(6));
        menu.add(navItem("deposit", "Deposit", "deposit"));
        menu.add(Box.createVerticalStrut(6));
        menu.add(navItem("withdraw", "Withdraw", "withdraw"));
        menu.add(Box.createVerticalStrut(6));
        menu.add(navItem("transfer", "Fund Transfer", "transfer"));
        menu.add(Box.createVerticalStrut(6));
        menu.add(navItem("history", "Transactions", "history"));
        menu.add(Box.createVerticalStrut(6));
        menu.add(navItem("profile", "Account", "profile"));

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 16, 20, 16));
        NavButton logout = new NavButton("Sign out", "logout");
        logout.bindKind("logout");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logout.addActionListener(e -> logout());
        JLabel edu = new JLabel("<html>Educational use only.<br>No real money movement.</html>");
        edu.setFont(AppTheme.font(11));
        edu.setForeground(AppTheme.MUTED);
        edu.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.add(logout);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(edu);

        side.add(brand, BorderLayout.NORTH);
        side.add(menu, BorderLayout.CENTER);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private NavButton navItem(String key, String label, String icon) {
        NavButton button = new NavButton(label, icon);
        button.bindKind(icon);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.addActionListener(e -> showPage(key));
        nav.put(key, button);
        return button;
    }

    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(AppTheme.NAVY);
        main.add(buildTopBar(), BorderLayout.NORTH);

        content.setOpaque(false);
        DashboardPanel dashboard = new DashboardPanel(this);
        DepositPanel deposit = new DepositPanel(this);
        WithdrawPanel withdraw = new WithdrawPanel(this);
        TransferPanel transfer = new TransferPanel(this);
        HistoryPanel history = new HistoryPanel(this);
        ProfilePanel profile = new ProfilePanel(this);
        pages.put("dashboard", dashboard);
        pages.put("deposit", deposit);
        pages.put("withdraw", withdraw);
        pages.put("transfer", transfer);
        pages.put("history", history);
        pages.put("profile", profile);
        content.add(dashboard, "dashboard");
        content.add(deposit, "deposit");
        content.add(withdraw, "withdraw");
        content.add(transfer, "transfer");
        content.add(history, "history");
        content.add(profile, "profile");

        main.add(content, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppTheme.NAVY);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.LINE),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel who = new JPanel();
        who.setOpaque(false);
        who.setLayout(new BoxLayout(who, BoxLayout.Y_AXIS));
        nameLabel.setFont(AppTheme.fontBold(18));
        nameLabel.setForeground(AppTheme.TEXT);
        nameLabel.setText(account.getHolderName());
        accountLabel.setFont(AppTheme.font(13));
        accountLabel.setForeground(AppTheme.MUTED);
        accountLabel.setText("A/C  " + account.getAccountNumber());
        who.add(nameLabel);
        who.add(accountLabel);

        clockLabel.setFont(AppTheme.font(13));
        clockLabel.setForeground(AppTheme.MUTED);

        RoundedButton quickDeposit = RoundedButton.primary("Quick deposit");
        quickDeposit.setPreferredSize(new Dimension(150, 38));
        quickDeposit.addActionListener(e -> showPage("deposit"));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);
        right.add(clockLabel);
        right.add(quickDeposit);

        bar.add(who, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }
}
