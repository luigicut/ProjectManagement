SELECT C.CFmembro, nome, cognome, impiegato=0 AS amministratore
FROM membro AS M, composto_da AS C
WHERE C.CFmembro = M.CFmembro AND C.nome_team='UNISAcoding'
ORDER BY M.impiegato ASC;