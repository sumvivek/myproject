import React from 'react'

const Suggestions = (props) => {
  const options = props.users.map(r => (
    <li key={r.id}>
      {r.link}
    </li>
  ))
  return <ul>{options}</ul>
}

export default Suggestions