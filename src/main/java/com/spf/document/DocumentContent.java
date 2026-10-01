package com.spf.document;

import java.util.List;

/**
 * Contenuto grezzo letto da un file.
 * - CSV / Excel: "rows" contiene le righe, ogni riga è una lista di celle (testo)
 * - PDF: "text" contiene il testo estratto
 */
public record DocumentContent(
        String fileName,
        String type,
        List<List<String>> rows,
        String text
) {
}
