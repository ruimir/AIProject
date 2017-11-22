;Definição de mensagens
(deftemplate ACLMessage (slot communicative-act) (slot sender) (multislot receiver)
              (slot reply-with) (slot in-reply-to) (slot envelope)
              (slot conversation-id) (slot protocol)
              (slot language) (slot ontology) (slot content)
              (slot encoding) (multislot reply-to) (slot reply-by))


;TODO:implementar no stationagent -> tera de ser feito um bind

(deftemplate Station (slot capacity) (slot load))



(defrule welcome-toddlers2
    "Give a special greeting to young children"
    (person {age < 3})
    ?a <- (Station (capacity ?c) (load ?l))
    =>
    (printout t "test" ?c ?l crlf)
    (printout t "Hello, little one!" crlf))

;capacity= lugares total
;load=lugares ocupados
;reduction= "redução", de forma a permitir maiores descontos
(deffunction calculateOffer (?capacity ?load ?reduction)
  (bind ?rate (/ ?load ?capacity)
  (bind ?inverse (- 1 ?rate))
  (bing ?result (* ?reduction ?inverse))
  (return ?result)))


; Envio de propostas
(defrule proposal
 ?m <- (ACLMessage (communicative-act INFORM) (sender ?s) (content ?c) (receiver ?r))
 ?st <- (Station (capacity ?capacity) (load ?load))
 (MyAgent (name ?n))
 =>
 (bind ?offer (calculateOffer ?capacity ?load 0.6))
 (assert (ACLMessage (communicative-act ACCEPT-PROPOSAL) (sender ?n) (receiver ?s) (content ?offer) ))
 (retract ?m)
)

;se recusar oferta, verifica se tiver espaço
;se tiver, aumenta desconto


(defrule inceaseDiscount
 "Supondo que podemos ainda dar ofertas, vamos dar uma oferta ligeiramente melhor"
 ?m <- (ACLMessage (communicative-act  REJECT-PROPOSAL) (sender ?s) (content ?c) (receiver ?r) {content = false})
 (MyAgent (name ?n))
 =>
 (bind ?offer (calculateOffer ?capacity ?load 0.8))
 (assert (ACLMessage (communicative-act ACCEPT-PROPOSAL) (sender ?n) (receiver ?s) (content ?offer) ))
 (retract ?m)
)

;devovulção de bicicleta
;ocupar lugar

(defrule returnBike
 ?m <- (ACLMessage (communicative-act  INFORM) (sender ?s) (content ?c) (receiver ?r) {content = return})
 (MyAgent (name ?n))
 ?st <- (Station (capacity ?capacity) (load ?load))
 =>
 (modify ?st (load (+ ?load 1)))
 ;se precisares de mandar mensagem avisa
)


;levantamento de bicicleta
;liberta lugar


(defrule liftBike
 ?m <- (ACLMessage (communicative-act INFORM) (sender ?s) (content ?c) (receiver ?r) {content = lift})
 (MyAgent (name ?n))
 ?st <- (Station (capacity ?capacity) (load ?load))
 =>
 (modify ?st (load (- ?load 1)))
)



(defrule send-a-message
    (MyAgent (name ?n))
    ?m <-(ACLMessage(sender ?n) (receiver ?r) (content ?c) (communicative-act ?ca))
    =>
    (printout t "Time to send a message!" crlf)
     ;(printout t "Sender " ?n crlf)
      ;(printout t "Receiver " ?r crlf)
       ;(printout t "Content " ?c crlf)
        ;(printout t "Performative " ?ca crlf)

    (send ?m) (retract ?m) )



(watch facts)
;(watch all)


(reset)