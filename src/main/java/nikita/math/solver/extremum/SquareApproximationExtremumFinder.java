package nikita.math.solver.extremum;

import java.math.BigDecimal;

public class SquareApproximationExtremumFinder extends ExtremumFinder {

	private static final int extraPrecision = 2;

//	public static Minimum minimum(Expression expression, Interval interval, BigDecimal x1, BigDecimal step, Precision functionPrecision,
//			Precision xPrecision) {
//
//		// Определяем масштаб для вычислений
//		int scale = functionPrecision.getPrecision().intValue() + extraPrecision;
//
//		// Определяем границы интервала
//		BigDecimal xLeft = interval.getLeft();
//		BigDecimal xRight = interval.getRight();
//
//		// Начальная точка x1Candidate, которая должна быть в пределах интервала
//		BigDecimal x1Candidate = x1;
//		if (x1Candidate.compareTo(xLeft) < 0) {
//			x1Candidate = xLeft;
//		} else if (x1Candidate.compareTo(xRight) > 0) {
//			x1Candidate = xRight;
//		}
//
//		// Вычисляем начальные точки x2 и x3
//		BigDecimal x2 = x1Candidate.add(step, new MathContext(scale, RoundingMode.HALF_UP));
//		BigDecimal x3 = x1Candidate.add(step.multiply(new BigDecimal("2")), new MathContext(scale, RoundingMode.HALF_UP));
//
//		// Если x3 выходит за пределы интервала, корректируем x3 и x2
//		if (x3.compareTo(xRight) > 0) {
//			x3 = xRight;
//			x2 = x1Candidate.add(xRight.subtract(x1Candidate).divide(new BigDecimal("2"), scale, RoundingMode.HALF_UP));
//		}
//
//		int iteration = 0;
//		logHeader();
//
//		// Основной цикл вычисления минимума
//		while (true) {
//			// Вычисляем значения функции в точках x1Candidate, x2 и x3
//			BigDecimal f1 = NMath.getBigDecimal(NMath.replaceAll(expression, x1Candidate.toPlainString()),
//					functionPrecision, scale);
//			BigDecimal f2 = NMath.getBigDecimal(NMath.replaceAll(expression, x2.toPlainString()), functionPrecision,
//					scale);
//			BigDecimal f3 = NMath.getBigDecimal(NMath.replaceAll(expression, x3.toPlainString()), functionPrecision,
//					scale);
//
//			// Вычисляем разности между точками
//			BigDecimal diff21 = x2.subtract(x1Candidate, new MathContext(scale, RoundingMode.HALF_UP));
//			BigDecimal diff23 = x2.subtract(x3, new MathContext(scale, RoundingMode.HALF_UP));
//
//			BigDecimal numerator = (diff21.multiply(diff21, new MathContext(scale, RoundingMode.HALF_UP)))
//					.multiply(f2.subtract(f3, new MathContext(scale, RoundingMode.HALF_UP)), new MathContext(scale, RoundingMode.HALF_UP))
//					.subtract((diff23.multiply(diff23, new MathContext(scale, RoundingMode.HALF_UP))).multiply(
//							f2.subtract(f1, new MathContext(scale, RoundingMode.HALF_UP)), new MathContext(scale, RoundingMode.HALF_UP)),
//							new MathContext(scale, RoundingMode.HALF_UP));
//
//			BigDecimal denominator = diff21
//					.multiply(f2.subtract(f3, new MathContext(scale, RoundingMode.HALF_UP)), new MathContext(scale, RoundingMode.HALF_UP))
//					.subtract(diff23.multiply(f2.subtract(f1, new MathContext(scale, RoundingMode.HALF_UP)),
//							new MathContext(scale, RoundingMode.HALF_UP)), new MathContext(scale, RoundingMode.HALF_UP));
//
//			// Если знаменатель равен нулю, перезапускаем вычисление с новой начальной
//			// точкой
//			if (denominator.compareTo(BigDecimal.ZERO) == 0) {
//				BigDecimal newX1 = chooseLowestX(x1Candidate, f1, x2, f2, x3, f3);
//				System.out.println("Знаменатель равен 0. Перезапуск с новой начальной точкой: " + newX1.toPlainString());
//				return minimum(expression, interval, newX1, step, functionPrecision, xPrecision);
//			}
//
//			BigDecimal adjustment = numerator.divide(
//					denominator.multiply(new BigDecimal("2"), new MathContext(scale, RoundingMode.HALF_UP)), scale, RoundingMode.HALF_UP);
//			BigDecimal xHat = x2.subtract(adjustment, new MathContext(scale, RoundingMode.HALF_UP)).setScale(scale, RoundingMode.HALF_UP);
//
//			logStep(iteration, xHat, f2);
//
//			// Вычисляем значение функции в новой точке xMin
//			BigDecimal fCandidate = NMath.getBigDecimal(NMath.replaceAll(expression, xHat.toPlainString()),
//					functionPrecision, scale);
//
//			// Определяем минимальное значение функции и соответствующую точку
//			BigDecimal Xmin = x1Candidate;
//			BigDecimal Fmin = f1;
//			if (f2.compareTo(Fmin) < 0) {
//				Xmin = x2;
//				Fmin = f2;
//			}
//			if (f3.compareTo(Fmin) < 0) {
//				Xmin = x3;
//				Fmin = f3;
//			}
//
//			// Проверяем условия остановки
//			boolean condition1 = fCandidate.compareTo(BigDecimal.ZERO) != 0
//					&& (Fmin.subtract(fCandidate).abs().divide(fCandidate.abs(), scale, RoundingMode.HALF_UP))
//							.compareTo(functionPrecision.getAccuracy()) < 0;
//			boolean condition2 = xHat.compareTo(BigDecimal.ZERO) != 0
//					&& (Xmin.subtract(xHat).abs().divide(xHat.abs(), scale, RoundingMode.HALF_UP)).compareTo(xPrecision.getAccuracy()) < 0;
//
//			// Если оба условия выполнены, возвращаем найденный минимум
//			if (condition1 && condition2) {
//				MathContext mc = new MathContext(functionPrecision.getPrecision().intValue());
//				return new Minimum(new BigDecimal(Xmin.toString(), mc), Fmin);
//			}
//
//			// Если xHat выходит за пределы интервала, перезапускаем вычисление с новой
//			// начальной точкой
//			if (xHat.compareTo(findHighest(x1Candidate, x2, x3)) < 0 || xHat.compareTo(findLowest(x1Candidate, x2, x3)) > 0) {
//				System.out.println("xMin (" + xHat.toPlainString() + ") не находится в пределах интервала [" + x1Candidate.toPlainString()
//						+ "; " + x3.toPlainString() + "]. Перезапуск с новой начальной точкой.");
//				return minimum(expression, interval, xHat, step, functionPrecision, xPrecision);
//			}
//
//			BigDecimal fHat = NMath.getBigDecimal(NMath.replaceAll(expression, xHat.toString()), functionPrecision);
//			ArrayList<BigDecimal> sortedLeft = (ArrayList<BigDecimal>) List.of(x1Candidate, x2, x3, xHat).stream()
//					.filter(x -> x.compareTo(fHat) <= 0).sorted().collect(Collectors.toList());
//			ArrayList<BigDecimal> sortedRight = (ArrayList<BigDecimal>) List.of(x1Candidate, x2, x3, xHat).stream()
//					.filter(x -> x.compareTo(fHat) <= 0).sorted().collect(Collectors.toList());
//			@SuppressWarnings("unused")
//			BigDecimal left = sortedLeft.get(sortedLeft.size() - 1);
//			@SuppressWarnings("unused")
//			BigDecimal right = sortedRight.get(0);
//
//			if (fHat.compareTo(f2) > 0) {
//				x1Candidate = x2;
//				x2 = xHat;
//				BigDecimal temp = xHat.add(step, new MathContext(scale, RoundingMode.HALF_UP));
//				x3 = temp.compareTo(xRight) > 0 ? xRight : temp;
//			} else {
//				x3 = x2;
//				x2 = xHat;
//				BigDecimal temp = xHat.subtract(step, new MathContext(scale, RoundingMode.HALF_UP));
//				x1Candidate = temp.compareTo(xLeft) < 0 ? xLeft : temp;
//			}
//			iteration++;
//		}
//	}

	// Метод для выбора точки с наименьшим значением функции
	private static BigDecimal chooseLowestX(BigDecimal x1, BigDecimal f1, BigDecimal x2, BigDecimal f2, BigDecimal x3, BigDecimal f3) {
		BigDecimal bestX = x1;
		BigDecimal bestF = f1;
		if (f2.compareTo(bestF) < 0) {
			bestX = x2;
			bestF = f2;
		}
		if (f3.compareTo(bestF) < 0) {
			bestX = x3;
		}
		return bestX;
	}

	// Метод для нахождения наименьшего из трёх значений
	public static BigDecimal findLowest(BigDecimal a, BigDecimal b, BigDecimal c) {
		BigDecimal min = a;
		if (b.compareTo(min) < 0) {
			min = b;
		}
		if (c.compareTo(min) < 0) {
			min = c;
		}
		return min;
	}

	// Метод для нахождения наибольшего из трёх значений
	public static BigDecimal findHighest(BigDecimal a, BigDecimal b, BigDecimal c) {
		BigDecimal max = a;
		if (b.compareTo(max) > 0) {
			max = b;
		}
		if (c.compareTo(max) > 0) {
			max = c;
		}
		return max;
	}

	// Метод для вывода заголовка таблицы
	private static void logHeader() {
		String header = String.format("%-10s | %-20s | %-20s", "Iteration", "x", "f(x)");
		System.out.println(header);
		System.out.println(new String(new char[header.length()]).replace('\0', '-'));
	}

	// Метод для вывода шага итерации
	private static void logStep(int iteration, BigDecimal x, BigDecimal fx) {
		String line = String.format("%-10d | %-20s | %-20s", iteration, x.toPlainString(), fx.toPlainString());
		System.out.println(line);
	}
}
