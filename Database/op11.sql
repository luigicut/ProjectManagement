SELECT DISTINCT M.nome, M.cognome
FROM membro AS M, team AS T, lavora_su_progetto AS L, commissionato_da as C, composto_da AS CT, progetto AS P
WHERE M.CFmembro = CT.CFmembro AND T.nome = CT.nome_team AND T.nome = L.nome_team 
AND L.IDprogetto = P.IDprogetto AND P.IDprogetto = C.IDprogetto
AND C.CFcommittente ='BRMMRG83C15F205V' AND M.CFmembro IN(SELECT CFmembro
														  FROM lavora_su_task as L,Commissionato_da as C, progetto as p,task as T
														  WHERE L.taskID=T.taskID AND T.IDprogetto=L.IDprogetto 
                                                          AND T.IDprogetto=P.IDprogetto AND C.IDprogetto=P.IDprogetto 
														  GROUP BY(L.CFmembro) HAVING COUNT(*)=2);


/*Op11: trovare nome e cognome dei membri che hanno lavorano su 2 progetti assegnati dallo stesso committente
*/