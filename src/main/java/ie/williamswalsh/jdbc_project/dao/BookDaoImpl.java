package ie.williamswalsh.jdbc_project.dao;

import ie.williamswalsh.jdbc_project.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class BookDaoImpl implements BookDao {


    public static final String SELECT_BOOK_BY_ID_PREPARED_STATEMENT = "SELECT * from book WHERE id=?";
    public static final String SELECT_BOOK_BY_TITLE_PREPARED_STATEMENT = "SELECT * from book WHERE title=?";
    public static final String INSERT_BOOK_PREPARED_STATEMENT = "INSERT INTO book (title, isbn, publisher) VALUES (?, ?, ?)";
    private static final String UPDATE_BOOK_PREPARED_STATEMENT = "UPDATE book SET title=?, isbn=?, publisher=? WHERE id=?";
    private static final String DELETE_BOOK_PREPARED_STATEMENT = "DELETE FROM book WHERE id=?";

    private final DataSource source;

    @Autowired
    public BookDaoImpl(DataSource source) {
        this.source = source;
    }

    @Override
    public Book getById(Long id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = source.getConnection();
            ps = conn.prepareStatement(SELECT_BOOK_BY_ID_PREPARED_STATEMENT);
            ps.setLong(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return getBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, rs);
        }
        return null;
    }

    private Book getBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setIsbn(rs.getString("isbn"));
        book.setPublisher(rs.getString("publisher"));
        return book;
    }

    private void closeAll(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if(rs != null) {
                rs.close();
            }

            if(ps != null) {
                ps.close();
            }

            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Book findBookByTitle(String title) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = source.getConnection();
            ps = conn.prepareStatement(SELECT_BOOK_BY_TITLE_PREPARED_STATEMENT);
            ps.setString(1, title);
            rs = ps.executeQuery();

            if (rs.next()) {
                return getBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, rs);
        }
        return null;
    }

    @Override
    public Book saveBook(Book book) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            conn = source.getConnection();
            ps = conn.prepareStatement(INSERT_BOOK_PREPARED_STATEMENT);

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setString(3, book.getPublisher());
            ps.execute();

            Statement statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");

            if(resultSet.next()) {
                long lastInsertIndex = resultSet.getLong(1);
                return this.getById(lastInsertIndex);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, resultSet);
        }
        return null;
    }

    @Override
    public Book updateBook(Book book) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            conn = source.getConnection();
            ps = conn.prepareStatement(UPDATE_BOOK_PREPARED_STATEMENT);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setString(3, book.getPublisher());
            ps.setLong(4, book.getId());
            ps.execute();

            Statement statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");

//            MySQL/H2 only feature - SELECT LAST_INSERT_ID
            if(resultSet.next()) {
                long lastInsertIndex = resultSet.getLong(1);
                return this.getById(lastInsertIndex);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, resultSet);
        }
        return null;
    }

    @Override
    public void deleteBookById(Long id) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = source.getConnection();
            ps = conn.prepareStatement(DELETE_BOOK_PREPARED_STATEMENT);
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, ps, null);
        }
    }
}
