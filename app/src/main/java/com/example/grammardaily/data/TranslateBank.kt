package com.example.grammardaily.data

data class TranslateTask(
    val topicId: String,
    val native: String,
    val english: String
)

object TranslateBank {

    val languages: List<Pair<String, String>> = listOf(
        "es" to "Spanish",
        "ar" to "Arabic",
        "fr" to "French",
        "pt" to "Portuguese",
        "tr" to "Turkish",
        "hi" to "Hindi"
    )

    fun languageName(code: String): String =
        languages.firstOrNull { it.first == code }?.second ?: code

    val sentences: Map<String, List<TranslateTask>> = mapOf(
        "es" to listOf(
            TranslateTask("A2:past-simple", "Ayer llam\u00e9 a mi primo.", "Yesterday, I called my cousin."),
            TranslateTask("A2:going-to", "Ma\u00f1ana voy a limpiar mi cuarto.", "Tomorrow, I am going to clean my room."),
            TranslateTask("B1:present-perfect", "He visitado tres pa\u00edses.", "I have visited three countries."),
            TranslateTask("B1:conditionals-1-2", "Si llueve, me quedar\u00e9 en casa.", "If it rains, I will stay home."),
            TranslateTask("B1:modals", "Tienes que parar en el sem\u00e1foro.", "You must stop at the red light."),
            TranslateTask("B1:connectors", "Estaba cansado, as\u00ed que me fui a dormir.", "I was tired, so I went to sleep."),
            TranslateTask("B2:passive", "El puente fue cerrado por la tormenta.", "The bridge was closed by the storm."),
            TranslateTask("B2:unreal-past", "Ojal\u00e1 hubiera estudiado m\u00e1s.", "I wish I had studied more."),
            TranslateTask("A2:past-continuous", "Estaba cocinando cuando son\u00F3 el tel\u00E9fono.", "I was cooking when the phone rang."),
            TranslateTask("B1:present-perfect-continuous", "Llevo tres a\u00F1os estudiando ingl\u00E9s.", "I have been studying English for three years."),
        ),
        "fr" to listOf(
            TranslateTask("A2:past-simple", "Hier, j'ai appel\u00e9 mon cousin.", "Yesterday, I called my cousin."),
            TranslateTask("A2:going-to", "Demain, je vais nettoyer ma chambre.", "Tomorrow, I am going to clean my room."),
            TranslateTask("B1:present-perfect", "J'ai visit\u00e9 trois pays.", "I have visited three countries."),
            TranslateTask("B1:conditionals-1-2", "S'il pleut, je resterai \u00e0 la maison.", "If it rains, I will stay home."),
            TranslateTask("B1:modals", "Tu dois t'arr\u00eater au feu rouge.", "You must stop at the red light."),
            TranslateTask("B1:connectors", "J'\u00e9tais fatigu\u00e9, alors je suis all\u00e9 dormir.", "I was tired, so I went to sleep."),
            TranslateTask("B2:passive", "Le pont a \u00e9t\u00e9 ferm\u00e9 par la temp\u00eate.", "The bridge was closed by the storm."),
            TranslateTask("B2:unreal-past", "Si seulement j'avais \u00e9tudi\u00e9 plus.", "I wish I had studied more."),
            TranslateTask("A2:past-continuous", "Je cuisinais quand le t\u00E9l\u00E9phone a sonn\u00E9.", "I was cooking when the phone rang."),
            TranslateTask("B1:present-perfect-continuous", "J'\u00E9tudie l'anglais depuis trois ans.", "I have been studying English for three years."),
        ),
        "pt" to listOf(
            TranslateTask("A2:past-simple", "Ontem liguei para o meu primo.", "Yesterday, I called my cousin."),
            TranslateTask("A2:going-to", "Amanh\u00e3 vou limpar o meu quarto.", "Tomorrow, I am going to clean my room."),
            TranslateTask("B1:present-perfect", "Visitei tr\u00eas pa\u00edses.", "I have visited three countries."),
            TranslateTask("B1:conditionals-1-2", "Se chover, ficarei em casa.", "If it rains, I will stay home."),
            TranslateTask("B1:modals", "Voc\u00ea tem de parar no sinal vermelho.", "You must stop at the red light."),
            TranslateTask("B1:connectors", "Estava cansado, ent\u00e3o fui dormir.", "I was tired, so I went to sleep."),
            TranslateTask("B2:passive", "A ponte foi fechada pela tempestade.", "The bridge was closed by the storm."),
            TranslateTask("B2:unreal-past", "Quem dera eu tivesse estudado mais.", "I wish I had studied more."),
            TranslateTask("A2:past-continuous", "Eu estava cozinhando quando o telefone tocou.", "I was cooking when the phone rang."),
            TranslateTask("B1:present-perfect-continuous", "Estudo ingl\u00EAs h\u00E1 tr\u00EAs anos.", "I have been studying English for three years."),
        ),
        "ar" to listOf(
            TranslateTask("A2:past-simple", "\u0627\u062a\u0635\u0644\u062a \u0628\u0627\u0628\u0646 \u0639\u0645\u064a \u0623\u0645\u0633.", "Yesterday, I called my cousin."),
            TranslateTask("A2:going-to", "\u0633\u0623\u0646\u0638\u0641 \u063a\u0631\u0641\u062a\u064a \u063a\u062f\u0627.", "Tomorrow, I am going to clean my room."),
            TranslateTask("B1:present-perfect", "\u0632\u0631\u062a \u062b\u0644\u0627\u062b\u0629 \u0628\u0644\u062f\u0627\u0646.", "I have visited three countries."),
            TranslateTask("B1:conditionals-1-2", "\u0625\u0630\u0627 \u0623\u0645\u0637\u0631\u062a\u060c \u0633\u0623\u0628\u0642\u0649 \u0641\u064a \u0627\u0644\u0628\u064a\u062a.", "If it rains, I will stay home."),
            TranslateTask("B1:modals", "\u064a\u062c\u0628 \u0623\u0646 \u062a\u062a\u0648\u0642\u0641 \u0639\u0646\u062f \u0627\u0644\u0625\u0634\u0627\u0631\u0629 \u0627\u0644\u062d\u0645\u0631\u0627\u0621.", "You must stop at the red light."),
            TranslateTask("B1:connectors", "\u0643\u0646\u062a \u0645\u062a\u0639\u0628\u0627\u060c \u0644\u0630\u0644\u0643 \u0630\u0647\u0628\u062a \u0644\u0644\u0646\u0648\u0645.", "I was tired, so I went to sleep."),
            TranslateTask("B2:passive", "\u0623\u064f\u063a\u0644\u0642 \u0627\u0644\u062c\u0633\u0631 \u0628\u0633\u0628\u0628 \u0627\u0644\u0639\u0627\u0635\u0641\u0629.", "The bridge was closed by the storm."),
            TranslateTask("B2:unreal-past", "\u0623\u062a\u0645\u0646\u0649 \u0644\u0648 \u062f\u0631\u0633\u062a \u0623\u0643\u062b\u0631.", "I wish I had studied more."),
            TranslateTask("A2:past-continuous", "\u0643\u0646\u062A \u0623\u0637\u0628\u062E \u0639\u0646\u062F\u0645\u0627 \u0631\u0646 \u0627\u0644\u0647\u0627\u062A\u0641.", "I was cooking when the phone rang."),
            TranslateTask("B1:present-perfect-continuous", "\u0623\u062F\u0631\u0633 \u0627\u0644\u0644\u063A\u0629 \u0627\u0644\u0625\u0646\u062C\u0644\u064A\u0632\u064A\u0629 \u0645\u0646\u0630 \u062B\u0644\u0627\u062B \u0633\u0646\u0648\u0627\u062A.", "I have been studying English for three years."),
        ),
        "hi" to listOf(
            TranslateTask("A2:past-simple", "\u092e\u0948\u0902\u0928\u0947 \u0915\u0932 \u0905\u092a\u0928\u0947 \u091a\u091a\u0947\u0930\u0947 \u092d\u093e\u0908 \u0915\u094b \u092b\u093c\u094b\u0928 \u0915\u093f\u092f\u093e\u0964", "Yesterday, I called my cousin."),
            TranslateTask("A2:going-to", "\u092e\u0948\u0902 \u0915\u0932 \u0905\u092a\u0928\u093e \u0915\u092e\u0930\u093e \u0938\u093e\u092b\u093c \u0915\u0930\u0942\u0901\u0917\u093e\u0964", "Tomorrow, I am going to clean my room."),
            TranslateTask("B1:present-perfect", "\u092e\u0948\u0902 \u0924\u0940\u0928 \u0926\u0947\u0936 \u0926\u0947\u0916 \u091a\u0941\u0915\u093e \u0939\u0942\u0901\u0964", "I have visited three countries."),
            TranslateTask("B1:conditionals-1-2", "\u0905\u0917\u0930 \u092c\u093e\u0930\u093f\u0936 \u0939\u0941\u0908, \u0924\u094b \u092e\u0948\u0902 \u0918\u0930 \u0930\u0939\u0942\u0901\u0917\u093e\u0964", "If it rains, I will stay home."),
            TranslateTask("B1:modals", "\u0924\u0941\u092e\u094d\u0939\u0947\u0902 \u0932\u093e\u0932 \u092c\u0924\u094d\u0924\u0940 \u092a\u0930 \u0930\u0941\u0915\u0928\u093e \u091a\u093e\u0939\u093f\u090f\u0964", "You must stop at the red light."),
            TranslateTask("B1:connectors", "\u092e\u0948\u0902 \u0925\u0915\u093e \u0939\u0941\u0906 \u0925\u093e, \u0907\u0938\u0932\u093f\u090f \u092e\u0948\u0902 \u0938\u094b\u0928\u0947 \u091a\u0932\u093e \u0917\u092f\u093e\u0964", "I was tired, so I went to sleep."),
            TranslateTask("B2:passive", "\u092a\u0941\u0932 \u0924\u0942\u092b\u093c\u093e\u0928 \u0915\u0940 \u0935\u091c\u0939 \u0938\u0947 \u092c\u0902\u0926 \u0915\u0930 \u0926\u093f\u092f\u093e \u0917\u092f\u093e \u0925\u093e\u0964", "The bridge was closed by the storm."),
            TranslateTask("B2:unreal-past", "\u0915\u093e\u0936 \u092e\u0948\u0902\u0928\u0947 \u0914\u0930 \u092a\u0922\u093c\u093e\u0908 \u0915\u0940 \u0939\u094b\u0924\u0940\u0964", "I wish I had studied more."),
            TranslateTask("A2:past-continuous", "\u092E\u0948\u0902 \u0916\u093E\u0928\u093E \u092C\u0928\u093E \u0930\u0939\u093E \u0925\u093E \u091C\u092C \u092B\u093C\u094B\u0928 \u092C\u091C\u093E\u0964", "I was cooking when the phone rang."),
            TranslateTask("B1:present-perfect-continuous", "\u092E\u0948\u0902 \u0924\u0940\u0928 \u0938\u093E\u0932 \u0938\u0947 \u0905\u0902\u0917\u094D\u0930\u0947\u091C\u093C\u0940 \u0938\u0940\u0916 \u0930\u0939\u093E \u0939\u0942\u0901\u0964", "I have been studying English for three years."),
        ),
        "tr" to listOf(
            TranslateTask("A2:past-simple", "D\u00fcn kuzenimi arad\u0131m.", "Yesterday, I called my cousin."),
            TranslateTask("A2:going-to", "Yar\u0131n odam\u0131 temizleyece\u011fim.", "Tomorrow, I am going to clean my room."),
            TranslateTask("B1:present-perfect", "\u00dc\u00e7 \u00fclke gezdim.", "I have visited three countries."),
            TranslateTask("B1:conditionals-1-2", "Ya\u011fmur ya\u011farsa evde kalaca\u011f\u0131m.", "If it rains, I will stay home."),
            TranslateTask("B1:modals", "K\u0131rm\u0131z\u0131 \u0131\u015f\u0131kta durmal\u0131s\u0131n.", "You must stop at the red light."),
            TranslateTask("B1:connectors", "Yorgundum, bu y\u00fczden uyumaya gittim.", "I was tired, so I went to sleep."),
            TranslateTask("B2:passive", "K\u00f6pr\u00fc f\u0131rt\u0131na y\u00fcz\u00fcnden kapat\u0131ld\u0131.", "The bridge was closed by the storm."),
            TranslateTask("B2:unreal-past", "Ke\u015fke daha \u00e7ok \u00e7al\u0131\u015fsayd\u0131m.", "I wish I had studied more."),
            TranslateTask("A2:past-continuous", "Telefon \u00E7ald\u0131\u011F\u0131nda yemek yap\u0131yordum.", "I was cooking when the phone rang."),
            TranslateTask("B1:present-perfect-continuous", "\u00DC\u00E7 y\u0131ld\u0131r \u0130ngilizce \u00E7al\u0131\u015F\u0131yorum.", "I have been studying English for three years."),
        )
    )
}
