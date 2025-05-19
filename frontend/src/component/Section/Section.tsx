import React from "react";
const Section = ({ id, className = "", style = {}, children }) => {
    return (
        <section
            id={id}
        >
            {children}
        </section>
    );
};
export default Section;

