package com.example.financemanagement.service;

import com.example.financemanagement.dto.MonthlyReport;
import com.example.financemanagement.dto.YearlyReport;
import com.example.financemanagement.entity.CategoryType;
import com.example.financemanagement.entity.Transaction;
import com.example.financemanagement.entity.User;
import com.example.financemanagement.repository.TransactionRepository;
import com.example.financemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for generating financial reports.
 */
@Service
public class ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Generates a monthly financial report for the current user.
     * @param year The year of the report.
     * @param month The month of the report.
     * @return A map containing the report data.
     */
    @Transactional(readOnly = true)
    public MonthlyReport generateMonthlyReport(int year, int month) {
        User user = getCurrentUser();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);

        Map<String, BigDecimal> incomeByCategory = calculateTotalsByCategory(transactions, CategoryType.INCOME);
        Map<String, BigDecimal> expensesByCategory = calculateTotalsByCategory(transactions, CategoryType.EXPENSE);

        BigDecimal totalIncome = incomeByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalExpenses = expensesByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netSavingsRaw = totalIncome.subtract(totalExpenses);
        BigDecimal netSavings = (netSavingsRaw.compareTo(BigDecimal.ZERO) == 0)
                ? BigDecimal.ZERO
                : netSavingsRaw.setScale(2, RoundingMode.HALF_UP);

        return new MonthlyReport(month, year, incomeByCategory, expensesByCategory, netSavings);
    }

    /**
     * Generates a yearly financial report for the current user.
     * @param year The year of the report.
     * @return A map containing the report data.
     */
    @Transactional(readOnly = true)
    public YearlyReport generateYearlyReport(int year) {
        User user = getCurrentUser();
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);

        Map<String, BigDecimal> incomeByCategory = calculateTotalsByCategory(transactions, CategoryType.INCOME);
        Map<String, BigDecimal> expensesByCategory = calculateTotalsByCategory(transactions, CategoryType.EXPENSE);

        BigDecimal totalIncome = incomeByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalExpenses = expensesByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netSavingsRaw = totalIncome.subtract(totalExpenses);
        BigDecimal netSavings = (netSavingsRaw.compareTo(BigDecimal.ZERO) == 0)
                ? BigDecimal.ZERO
                : netSavingsRaw.setScale(2, RoundingMode.HALF_UP);

        return new YearlyReport(year, incomeByCategory, expensesByCategory, netSavings);
    }

    private Map<String, BigDecimal> calculateTotalsByCategory(List<Transaction> transactions, CategoryType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
} 