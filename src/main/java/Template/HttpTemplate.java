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
		File file = new File("templates/views/" + filename);
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


	public static Map<String, Object> bodyToMap(String body) {

		Map<String, Object> map = new java.util.HashMap<>();

		if (body == null || body.isBlank())
			return map;

		body = body.trim();

		if (body.startsWith("{"))
			body = body.substring(1);
		if (body.endsWith("}"))
			body = body.substring(0, body.length() - 1);

		String[] pairs = body.split(",");

		for (String pair : pairs) {

			String[] kv = pair.split(":", 2);
			if (kv.length != 2) continue;

			String key = kv[0].trim().replace("\"", "");
			String value = kv[1].trim();

			map.put(key, parseValue(value));
		}

		return map;
	}

	private static Object parseValue(String value) {

		if (value.startsWith("\"") && value.endsWith("\""))
			return value.substring(1, value.length() - 1);

		if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
			return Boolean.parseBoolean(value);

		try {
			if (value.contains("."))
				return Double.parseDouble(value);
			return Long.parseLong(value);
		} catch (Exception e) {
			return value;
		}
	}

}