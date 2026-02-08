package Template;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Field;

public class HttpTemplate {

	// -----------------------
	// View: retorna HTML
	// -----------------------
	public static String view(String filename) {
		// A pasta views/ está no nível do projeto
		File file = new File("template/views/" + filename);
		if (!file.exists()) {
			return "<h1>Erro 404: Arquivo não encontrado</h1>";
		}

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			StringBuilder html = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				html.append(line).append("\n");
			}
			return html.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "<h1>Erro ao carregar a view</h1>";
		}
	}

	// -----------------------
	// JSON automático
	// -----------------------
	public static String json(Object obj) throws IllegalAccessException {
		if (obj instanceof Map) {
			return mapToJson((Map<?, ?>) obj);
		} else if (obj instanceof List) {
			return listToJson((List<?>) obj);
		} else {
			return objectToJson(obj);
		}
	}

	private static String mapToJson(Map<?, ?> map) {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			if (!first) sb.append(",");
			sb.append("\"").append(entry.getKey()).append("\":");
			sb.append("\"").append(entry.getValue()).append("\"");
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}

	private static String listToJson(List<?> list) throws IllegalAccessException {
		StringBuilder sb = new StringBuilder("[");
		boolean first = true;
		for (Object item : list) {
			if (!first) sb.append(",");
			sb.append(json(item));
			first = false;
		}
		sb.append("]");
		return sb.toString();
	}

	private static String objectToJson(Object obj) throws IllegalAccessException {
		Field[] fields = obj.getClass().getDeclaredFields();
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (Field f : fields) {
			f.setAccessible(true);
			if (!first) sb.append(",");
			sb.append("\"").append(f.getName()).append("\":");
			Object value = f.get(obj);
			if (value instanceof Number || value instanceof Boolean) {
				sb.append(value);
			} else {
				sb.append("\"").append(value).append("\"");
			}
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}

	// -----------------------
	// Auxiliares: parse de valores
	// -----------------------
	public static int toInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			throw new IllegalArgumentException("Valor não é int: " + s);
		}
	}

	public static long toLong(String s) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			throw new IllegalArgumentException("Valor não é long: " + s);
		}
	}

	public static double toDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			throw new IllegalArgumentException("Valor não é double: " + s);
		}
	}

	public static boolean toBool(String s) {
		return Boolean.parseBoolean(s);
	}

}