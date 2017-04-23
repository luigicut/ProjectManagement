SELECT DISTINCT nome,cognome
FROM membro AS M, lavora_su_task as L
WHERE M.CFmembro=L.CFmembro AND M.impiegato ='1' AND  L.tempo_speso>20 AND
L.CFmembro IN (SELECT CFmembro
                FROM lavora_su_task as L
                WHERE L.tempo_speso>20
                Group By (CFmembro) HAVING COUNT(*)>1);