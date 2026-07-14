package com.agon.app.data

import com.agon.app.R

object SampleData {

    val diseases = listOf(
        CropDisease(
            id = "healthy",
            name = "Folha saudável",
            cropType = "Geral",
            severity = Severity.SAUDAVEL,
            symptoms = "Cor verde uniforme, sem manchas, textura firme.",
            treatment = "Nenhum tratamento necessário. Continue a rotina de rega e adubação.",
            prevention = "Mantenha espaçamento adequado entre plantas e monitorize semanalmente.",
        ),
        CropDisease(
            id = "blight",
            name = "Requeima (Míldio)",
            cropType = "Tomate / Batata",
            severity = Severity.ALTA,
            symptoms = "Manchas castanhas irregulares nas folhas, que aumentam rapidamente em tempo húmido.",
            treatment = "Remova as folhas afetadas e aplique fungicida à base de cobre. Evite molhar as folhas ao regar.",
            prevention = "Rotação de culturas, espaçamento adequado e variedades resistentes.",
        ),
        CropDisease(
            id = "rust",
            name = "Ferrugem",
            cropType = "Milho / Feijão",
            severity = Severity.MEDIA,
            symptoms = "Pequenas pústulas alaranjadas ou castanhas na face inferior das folhas.",
            treatment = "Aplicar fungicida específico para ferrugem nas primeiras semanas de infeção.",
            prevention = "Use sementes certificadas e evite excesso de humidade na plantação.",
        ),
        CropDisease(
            id = "mosaic",
            name = "Vírus do Mosaico",
            cropType = "Mandioca / Hortícolas",
            severity = Severity.ALTA,
            symptoms = "Padrão de manchas amarelas e verdes tipo mosaico, folhas deformadas.",
            treatment = "Não há cura direta; remova plantas infetadas para evitar propagação por insetos vetores.",
            prevention = "Controlo de moscas-brancas e uso de material de plantação certificado.",
        ),
    )

    fun diseaseById(id: String) = diseases.first { it.id == id }

    val sampleLeaves = listOf(
        SampleLeaf("leaf1", "Folha saudável", R.drawable.plant_healthy, null),
        SampleLeaf("leaf2", "Folha com manchas", R.drawable.plant_blight, "blight"),
        SampleLeaf("leaf3", "Folha com ferrugem", R.drawable.plant_rust, "rust"),
        SampleLeaf("leaf4", "Folha amarelada", R.drawable.plant_mosaic, "mosaic"),
    )

    val weatherForecast = listOf(
        WeatherDay("Hoje", "12 Jun", WeatherCondition.SOL, 29, 18, 10, 45, "Bom dia para irrigar de manhã cedo."),
        WeatherDay("Amanhã", "13 Jun", WeatherCondition.NUBLADO, 27, 17, 30, 55, "Céu nublado — reduza a rega em 20%."),
        WeatherDay("Quarta", "14 Jun", WeatherCondition.CHUVA, 24, 16, 75, 80, "Chuva prevista: adie a aplicação de fertilizante."),
        WeatherDay("Quinta", "15 Jun", WeatherCondition.CHUVA, 23, 15, 85, 85, "Risco de encharcamento — verifique drenagem."),
        WeatherDay("Sexta", "16 Jun", WeatherCondition.TEMPESTADE, 22, 15, 90, 88, "Tempestade possível: proteja plantações jovens."),
        WeatherDay("Sábado", "17 Jun", WeatherCondition.NUBLADO, 25, 16, 40, 60, "Bom para trabalhos de capina."),
        WeatherDay("Domingo", "18 Jun", WeatherCondition.SOL, 28, 17, 5, 42, "Ideal para colheita e secagem de grãos."),
    )

    val marketPrices = listOf(
        MarketPrice("Milho", "Cereais", "saco 50kg", 12500, PriceTrend.SUBIU, 8),
        MarketPrice("Feijão", "Leguminosas", "saco 50kg", 21000, PriceTrend.SUBIU, 4),
        MarketPrice("Mandioca", "Tubérculos", "saco 60kg", 9800, PriceTrend.ESTAVEL, 0),
        MarketPrice("Batata-doce", "Tubérculos", "saco 50kg", 8600, PriceTrend.DESCEU, 5),
        MarketPrice("Tomate", "Hortícolas", "caixa 20kg", 15500, PriceTrend.SUBIU, 12),
        MarketPrice("Cebola", "Hortícolas", "saco 25kg", 11200, PriceTrend.DESCEU, 3),
        MarketPrice("Arroz", "Cereais", "saco 50kg", 24500, PriceTrend.ESTAVEL, 0),
        MarketPrice("Banana", "Fruta", "cacho", 3500, PriceTrend.SUBIU, 6),
    )

    val farmingTips = listOf(
        FarmingTip(
            "tip1", "Rotação de culturas", "Solo",
            "Alterne culturas diferentes na mesma parcela a cada estação para preservar os nutrientes do solo e reduzir pragas.",
            "Mudai fitas dza mu munda umodzi pa nyengo iliyonse kuti mutakhala ndi phindu labwino.",
            "Chichewa (exemplo)",
        ),
        FarmingTip(
            "tip2", "Compostagem caseira", "Adubo",
            "Use restos de cozinha e folhas secas para criar adubo orgânico gratuito e melhorar a fertilidade do solo.",
            "Sebenzisa imfucuza yasekhitshini kanye lamahlamvu omileyo ukwenza umanyolo wemvelo ngamahhala.",
            "Ndebele (exemplo)",
        ),
        FarmingTip(
            "tip3", "Irrigação por gotejamento", "Água",
            "Reduza o desperdício de água até 60% usando sistemas simples de gotejamento feitos com garrafas furadas.",
            "Fungura amanzi ngokusetshenziswa kwezinsimbi ezincane ukuze kunciphe ukumoshwa kwamanzi.",
            "Zulu (exemplo)",
        ),
        FarmingTip(
            "tip4", "Controlo natural de pragas", "Pragas",
            "Plante flores de tagetes entre as culturas para repelir insetos de forma natural, sem químicos.",
            "Byalani intyatyambo zeTagetes phakathi kwezityalo ukuxhathisa izinambuzane ngokwendalo.",
            "Xhosa (exemplo)",
        ),
        FarmingTip(
            "tip5", "Armazenamento de grãos", "Pós-colheita",
            "Seque bem os grãos ao sol antes de armazenar e use sacos herméticos para evitar fungos e insetos.",
            "Yomisa imbewu kahle elangeni ungakayifaki esitsheni ukugwema ukubola nezinambuzane.",
            "Zulu (exemplo)",
        ),
    )

    val produceListings = listOf(
        ProduceListing("p1", "Joaquim Neto", "Milho", "2 toneladas", 12000, "saco 50kg", "Huambo, Angola", "2h", true),
        ProduceListing("p2", "Maria Sumbo", "Tomate", "500 kg", 15000, "caixa 20kg", "Benguela, Angola", "5h", true),
        ProduceListing("p3", "António Kanda", "Feijão", "1 tonelada", 20500, "saco 50kg", "Malanje, Angola", "1 dia", false),
        ProduceListing("p4", "Isabel Chissano", "Mandioca", "3 toneladas", 9500, "saco 60kg", "Uíge, Angola", "3h", true),
        ProduceListing("p5", "Domingos Bento", "Banana", "200 cachos", 3200, "cacho", "Cabinda, Angola", "6h", false),
    )

    // ---- BiscatoHub ----

    val workers = listOf(
        WorkerProfile(
            "w1", "Fernando Muatxi", SkillCategory.PEDREIRO,
            "15 anos de experiência em construção e reboco. Trabalho garantido e limpo.",
            "Viana, Luanda", 1.2, 4.8, 63, 120, "a partir de 8.000 Kz/dia", true,
            listOf(
                Review("Carla M.", 5, "Excelente trabalho, muito pontual!", "há 3 dias"),
                Review("Pedro S.", 5, "Recomendo, preço justo.", "há 1 semana"),
            ),
        ),
        WorkerProfile(
            "w2", "Vitória Kiala", SkillCategory.COSTUREIRA,
            "Costura e conserto de roupa, uniformes escolares e capulanas sob medida.",
            "Maianga, Luanda", 0.8, 4.9, 88, 200, "a partir de 1.500 Kz/peça", true,
            listOf(Review("João P.", 5, "Trabalho impecável e rápido.", "há 2 dias")),
        ),
        WorkerProfile(
            "w3", "Manuel Sacadura", SkillCategory.ELETRICISTA,
            "Instalação elétrica residencial, quadros e reparação de avarias.",
            "Talatona, Luanda", 3.5, 4.6, 41, 90, "a partir de 6.000 Kz/visita", true,
        ),
        WorkerProfile(
            "w4", "Adriano Bumba", SkillCategory.CARPINTEIRO,
            "Móveis sob medida, portas e reparações em madeira maciça.",
            "Cazenga, Luanda", 2.1, 4.7, 35, 70, "a partir de 10.000 Kz/projeto", false,
        ),
        WorkerProfile(
            "w5", "Ester Domingos", SkillCategory.EXPLICADOR,
            "Explicações de Matemática e Física para o ensino secundário.",
            "Rangel, Luanda", 1.6, 5.0, 22, 55, "a partir de 2.000 Kz/hora", true,
        ),
        WorkerProfile(
            "w6", "Ricardo Wamba", SkillCategory.CANALIZADOR,
            "Canalizador certificado, resolve fugas, entupimentos e instalações.",
            "Kilamba, Luanda", 4.0, 4.5, 29, 60, "a partir de 5.000 Kz/visita", false,
        ),
        WorkerProfile(
            "w7", "Domingas Fuca", SkillCategory.LIMPEZA,
            "Limpeza doméstica e de escritórios, produtos próprios.",
            "Viana, Luanda", 2.8, 4.8, 51, 130, "a partir de 4.000 Kz/dia", true,
        ),
        WorkerProfile(
            "w8", "Kiluanje Afonso", SkillCategory.JARDINAGEM,
            "Manutenção de jardins, poda e paisagismo para casas e empresas.",
            "Talatona, Luanda", 1.9, 4.4, 18, 40, "a partir de 3.500 Kz/dia", false,
        ),
    )

    val jobs = listOf(
        JobPost(
            "j1", "Preciso de pedreiro para reboco urgente", SkillCategory.PEDREIRO,
            "Reboco de uma parede exterior de 20m². Material já disponível no local.",
            "Viana, Luanda", 1.0, "10.000 - 15.000 Kz", "Sofia Neto", "há 20 min", true, JobStatus.ABERTO,
        ),
        JobPost(
            "j2", "Aula de explicação de Matemática 10ª classe", SkillCategory.EXPLICADOR,
            "Aluno com dificuldades em funções e equações. 2x por semana.",
            "Rangel, Luanda", 2.3, "2.500 Kz/hora", "Manuel Coxe", "há 1h", false, JobStatus.ABERTO,
        ),
        JobPost(
            "j3", "Conserto de vestido de festa", SkillCategory.COSTUREIRA,
            "Ajustar tamanho e trocar zíper de um vestido. Precisa até sexta-feira.",
            "Maianga, Luanda", 0.5, "2.000 Kz", "Beatriz Lopes", "há 3h", true, JobStatus.ABERTO,
        ),
        JobPost(
            "j4", "Instalação elétrica de loja nova", SkillCategory.ELETRICISTA,
            "Loja de 60m², precisa de instalação completa de tomadas e iluminação.",
            "Talatona, Luanda", 4.2, "40.000 Kz", "Empresa AngoShop", "há 5h", false, JobStatus.ABERTO,
        ),
        JobPost(
            "j5", "Móvel de cozinha sob medida", SkillCategory.CARPINTEIRO,
            "Preciso de armário de cozinha em madeira, medidas 2m x 2.4m.",
            "Cazenga, Luanda", 3.0, "35.000 Kz", "Rui Almeida", "há 1 dia", false, JobStatus.EM_ANDAMENTO,
        ),
        JobPost(
            "j6", "Desentupimento urgente de canalização", SkillCategory.CANALIZADOR,
            "Casa de banho entupida há dois dias, precisa resolver hoje.",
            "Kilamba, Luanda", 1.7, "6.000 Kz", "Ana Paula", "há 40 min", true, JobStatus.ABERTO,
        ),
        JobPost(
            "j7", "Limpeza pós-obra de apartamento", SkillCategory.LIMPEZA,
            "Apartamento T3 recém-pintado, precisa limpeza completa.",
            "Viana, Luanda", 2.5, "8.000 Kz", "Cristina Bento", "há 2h", false, JobStatus.ABERTO,
        ),
        JobPost(
            "j8", "Manutenção de jardim e poda de árvores", SkillCategory.JARDINAGEM,
            "Quintal de 300m² com relvado e árvores de fruto para podar.",
            "Talatona, Luanda", 3.8, "12.000 Kz", "Empresa Verde Vida", "há 1 dia", false, JobStatus.CONCLUIDO,
        ),
    )

    val sampleChat = listOf(
        ChatMessage("m1", false, "Boa tarde! Vi o seu perfil, preciso de um pedreiro para amanhã.", "09:14"),
        ChatMessage("m2", true, "Boa tarde! Posso sim, qual é o serviço exatamente?", "09:16"),
        ChatMessage("m3", false, "Reboco de uma parede de 20m², material já tenho.", "09:17"),
        ChatMessage("m4", true, "Perfeito, consigo fazer amanhã de manhã. Fica por 12.000 Kz.", "09:20"),
        ChatMessage("m5", false, "Combinado! Pode vir às 8h?", "09:22"),
        ChatMessage("m6", true, "Sim, estarei lá às 8h em ponto.", "09:23"),
    )
}
