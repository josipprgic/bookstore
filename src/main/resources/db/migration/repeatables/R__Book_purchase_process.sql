CREATE OR REPLACE FUNCTION bookstore.purchase_book(_username TEXT, bookname TEXT, _publishedDate TIMESTAMP, useLoyaltyPoints BOOLEAN, discount DECIMAL) RETURNS VOID AS
$$
DECLARE
    userId BIGINT;
    bookId BIGINT;
    bookPrice DECIMAL;
BEGIN

    SELECT id
    INTO userId
    FROM bookstore.user u
    WHERE u.username = _username;

    IF userId IS NULL THEN
        RAISE EXCEPTION 'User with that username does not exist';
    end if;

    SELECT id
    INTO bookId
    FROM bookstore.book b
    WHERE b.name = bookname
      AND b.published_date = _publishedDate::DATE;

    IF bookId IS NULL THEN
        RAISE EXCEPTION 'Book with that name and publishDate does not exist';
    end if;

    IF useLoyaltyPoints AND (SELECT u.loyalty_points FROM bookstore.user u WHERE u.id = userId) < 10 THEN
        RAISE EXCEPTION 'Passed user does not have enough loyalty points to purchase book for free';
    end if;

    IF useLoyaltyPoints THEN

        INSERT INTO bookstore.purchase (user_id, book_id, price)
        VALUES (userId, bookId, 0);

        UPDATE bookstore.user u
        SET loyalty_points = u.loyalty_points - 10
        WHERE u.id = userId;

        RETURN;
    end if;

    SELECT price
    INTO bookPrice
    FROM bookstore.book b
    WHERE b.id = bookId;

    INSERT INTO bookstore.purchase (user_id, book_id, price)
    VALUES (userId, bookId, bookPrice - bookPrice * discount);

    UPDATE bookstore.user u
    SET loyalty_points = u.loyalty_points + 1
    WHERE u.id = userId;

END;
$$
    LANGUAGE PLPGSQL;