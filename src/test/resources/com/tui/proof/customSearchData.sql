-- should return
INSERT INTO CLIENT (email, telephone, firstName, lastName)
VALUES ('adam@mail.com', '733 666 133', 'Adam', 'Klast');

INSERT INTO CLIENT(email, telephone, firstName, lastName)
 VALUES ('adam@gmail.com', '733 777 133', 'AdamZ', 'Ksecond');

-- should not return (mail ending)
INSERT INTO CLIENT (email, telephone, firstName, lastName)
VALUES ('adamA@mail.pl', '733 666 133', 'AdamA', 'Klast');
-- should not return (phone ending)
INSERT INTO CLIENT ( email, telephone, firstName, lastName)
VALUES ('adamB@mail.com', '733 666 134', 'AdamB', 'Klast');
-- should not return firstName
INSERT INTO CLIENT (email, telephone, firstName, lastName)
VALUES ('john@mail.com', '733 777 133', 'John', 'Klast');
-- should not return lastNameStart
INSERT INTO CLIENT (email, telephone, firstName, lastName)
VALUES ('adamC@mail.com', '733 888 133', 'AdamC', 'Last');


commit;


