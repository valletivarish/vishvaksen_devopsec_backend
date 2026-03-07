package com.mealplanner.api.service;

import com.mealplanner.api.dto.ForecastResponse;
import com.mealplanner.api.model.*;
import com.mealplanner.api.repository.MealPlanRepository;
import com.mealplanner.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ML Forecasting Service using Apache Commons Math SimpleRegression.
 * Predicts nutritional intake trends (calories, protein, carbs, fat)
 * for the next 7 days based on historical meal plan data.
 * Uses linear regression to fit a trend line to daily nutrient intake
 * and extrapolate future values with confidence scores.
 */
@Service
@RequiredArgsConstructor
public class ForecastService {

    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;

    /**
     * Generates nutritional trend forecasts for a user.
     * Analyzes all historical meal plans to build daily nutrient intake data,
     * then uses SimpleRegression to predict the next 7 days of intake
     * for each macronutrient (calories, protein, carbs, fat).
     */
    @Transactional(readOnly = true)
    public ForecastResponse generateForecast(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MealPlan> mealPlans = mealPlanRepository.findByUserId(user.getId());

        /* Aggregate daily nutritional data from all meal plans */
        Map<LocalDate, double[]> dailyNutrients = new TreeMap<>();

        for (MealPlan plan : mealPlans) {
            for (MealPlanEntry entry : plan.getEntries()) {
                /* Calculate the actual date from the meal plan start date and day of week */
                LocalDate entryDate = plan.getStartDate()
                        .plusDays(entry.getDayOfWeek().getValue() - plan.getStartDate().getDayOfWeek().getValue());
                if (entryDate.isBefore(plan.getStartDate())) {
                    entryDate = entryDate.plusWeeks(1);
                }

                double recipeCal = 0;
                double recipePro = 0;
                double recipeCar = 0;
                double recipeFa = 0;

                /* Sum nutritional values from all ingredients in the recipe */
                for (RecipeIngredient ri : entry.getRecipe().getRecipeIngredients()) {
                    double factor = ri.getQuantity() / 100.0;
                    recipeCal += ri.getIngredient().getCalories() * factor;
                    recipePro += ri.getIngredient().getProtein() * factor;
                    recipeCar += ri.getIngredient().getCarbs() * factor;
                    recipeFa += ri.getIngredient().getFat() * factor;
                }

                /* Accumulate daily totals for the calculated date */
                dailyNutrients.computeIfAbsent(entryDate, k -> new double[4]);
                dailyNutrients.get(entryDate)[0] += recipeCal;
                dailyNutrients.get(entryDate)[1] += recipePro;
                dailyNutrients.get(entryDate)[2] += recipeCar;
                dailyNutrients.get(entryDate)[3] += recipeFa;
            }
        }

        /* Build forecasts for each nutrient type */
        String[] nutrientNames = {"Calories", "Protein", "Carbs", "Fat"};
        List<ForecastResponse.NutrientForecast> forecasts = new ArrayList<>();
        double overallSlope = 0;
        double overallRSquared = 0;

        for (int n = 0; n < 4; n++) {
            SimpleRegression regression = new SimpleRegression();
            int index = 0;
            double sum = 0;

            /* Add data points to the regression model (x=day index, y=nutrient value) */
            for (Map.Entry<LocalDate, double[]> entry : dailyNutrients.entrySet()) {
                regression.addData(index, entry.getValue()[n]);
                sum += entry.getValue()[n];
                index++;
            }

            double average = index > 0 ? sum / index : 0;
            double rSquared = index > 2 ? Math.max(0, regression.getRSquare()) : 0;
            double slope = regression.getSlope();

            /* Predict values for the next 7 days */
            List<Double> predictedValues = new ArrayList<>();
            List<String> predictedDates = new ArrayList<>();
            LocalDate startDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (int day = 0; day < 7; day++) {
                double predicted = regression.predict(index + day);
                /* Ensure predicted values are non-negative */
                predictedValues.add(Math.max(0, Math.round(predicted * 100.0) / 100.0));
                predictedDates.add(startDate.plusDays(day).format(formatter));
            }

            /* Determine trend direction based on regression slope */
            String trend;
            if (slope > 0.5) {
                trend = "INCREASING";
            } else if (slope < -0.5) {
                trend = "DECREASING";
            } else {
                trend = "STABLE";
            }

            if (n == 0) {
                overallSlope = slope;
                overallRSquared = rSquared;
            }

            forecasts.add(ForecastResponse.NutrientForecast.builder()
                    .nutrientName(nutrientNames[n])
                    .currentAverage(Math.round(average * 100.0) / 100.0)
                    .predictedValues(predictedValues)
                    .predictedDates(predictedDates)
                    .trend(trend)
                    .rSquared(Math.round(rSquared * 10000.0) / 10000.0)
                    .build());
        }

        /* Determine overall trend direction from calorie trend */
        String overallTrend;
        if (overallSlope > 0.5) {
            overallTrend = "INCREASING";
        } else if (overallSlope < -0.5) {
            overallTrend = "DECREASING";
        } else {
            overallTrend = "STABLE";
        }

        return ForecastResponse.builder()
                .trendDirection(overallTrend)
                .confidenceScore(Math.round(overallRSquared * 10000.0) / 10000.0)
                .forecasts(forecasts)
                .build();
    }
}
